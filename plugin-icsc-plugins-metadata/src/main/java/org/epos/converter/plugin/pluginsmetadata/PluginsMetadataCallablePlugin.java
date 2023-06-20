package org.epos.converter.plugin.pluginsmetadata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.epos.converter.common.exception.PayloadProcessingException;
import org.epos.converter.common.java.CallableJavaPlugin;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.in.PluginsMetadataIcscSchema;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.Conversion;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.Execution;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.Mapping;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.PluginHeader;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.PluginHeader.ProxyType;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.PluginKey;
import org.epos.converter.plugin.pluginsmetadata.content.jackson.out.PluginsMetadataConverterSchema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PluginsMetadataCallablePlugin extends CallableJavaPlugin {
	
	public PluginsMetadataCallablePlugin(MappingDescriptor mapping) throws PluginConfigurationException {
		super(mapping);
	}

	@Override
	protected Optional<String> doInvoke(String payload) throws PayloadProcessingException
	{
		ObjectMapper objMapper = new ObjectMapper();
		
		try {
			var src = objMapper.readValue(payload, new TypeReference<List<PluginsMetadataIcscSchema>>() {});
			var dest = doConversionOfPojos(objMapper, src);	
			String outPayload = objMapper.writeValueAsString(dest);
			return Optional.ofNullable(outPayload);
		} catch (IOException e) { 
			throw new PayloadProcessingException("Failed to process payload due to problem(s) with its serialisation", e);
		}
	}

	private List<PluginsMetadataConverterSchema> doConversionOfPojos(ObjectMapper objMapper, List<PluginsMetadataIcscSchema> src) 
	{
		return src.parallelStream()
				.map(src_plugin -> {	
					List<String> repoArtifactLocations = getRepoArtifactsLocations(src_plugin);
					boolean repoAccessRestriction = getRepoAccessRestriction(src_plugin);
					String pluginInstallDir = getPluginInstallDir(src_plugin);

					// plugin key
					var pluginKey = new PluginKey()
							.withId(src_plugin.getIdentifier())
							.withVersion(src_plugin.getSoftwareVersion());
					
					ProxyType proxyType = ProxyType.fromValue(src_plugin.getProxyType());
					
					// plugin header					
					var pluginHeader = new PluginHeader()
							.withName(src_plugin.getName())
							.withRepoLocation(src_plugin.getDownloadURL())
							.withRepoArtifactLocations(repoArtifactLocations)
							.withRepoAccessRestriction(repoAccessRestriction)
							.withInstallLocation(pluginInstallDir)
							.withProxyType(proxyType);
							
					return new PluginsMetadataConverterSchema()
							.withPluginKey(pluginKey)
							.withPluginHeader(pluginHeader)
							.withConversions(deriveConversions(src_plugin));
				}).collect(Collectors.toList());
	}

	private boolean getRepoAccessRestriction(PluginsMetadataIcscSchema src_plugin) 
	{
		// currently assuming all repositories containing plugins have restricted access 
		return true;
	}

	private String getPluginInstallDir(PluginsMetadataIcscSchema src_plugin) 
	{
		String pluginKeyId = src_plugin.getIdentifier();	
		String pluginIdDirName = replaceFileSeparatorChars(pluginKeyId, "-");
		
		String pluginKeyVer = src_plugin.getSoftwareVersion();
		String pluginKeyVerDirName = replaceFileSeparatorChars(pluginKeyVer, "-");
		
		if (src_plugin.getLocation() != null) {
			// ensure paths are always treated as relative paths
			return src_plugin.getLocation().strip().replaceFirst("^/", "");
		} else {
			// infer a plug-in location if none specified
			return pluginIdDirName + "/" + pluginKeyVerDirName;
		}
	}

	private List<String> getRepoArtifactsLocations(PluginsMetadataIcscSchema src_plugin) 
	{
		ProxyType proxyType = ProxyType.fromValue(src_plugin.getProxyType());
		
		if (ProxyType.JAVA_REFLECTION.equals(proxyType)) {
			/* For Java Reflection plugins the assumption is made that the first 'requirements' token
			 *  will describe the location, within the repo, of the plugin artifacts (in addition to the 
			 *  relative location of the invocation of the artifcat within the Converter system).
			*/  
			String[] invocationDetails = src_plugin.getRequirements().split("\\s*;\\s*");
			String artifactDir =invocationDetails[0];
			String artifactFile = invocationDetails[1];
			return Collections.singletonList(artifactDir + artifactFile);
		} else {
			return Collections.emptyList();
		}
	}

	private static String replaceFileSeparatorChars(String text, String replacement) {
		return text.replaceAll("[\\\\|/]+", replacement);
	}

	private List<Conversion> deriveConversions(PluginsMetadataIcscSchema src) 
	{
		return src.getOperations().parallelStream()
				.map(src_op -> {
					String requestContentFormat = src.getAction().getObject().getEncodingFormat();
					String responseContentFormat = src.getAction().getResult().getEncodingFormat();
					
					Mapping mapping = new Mapping()
						.withRequestType(src_op)
						.withRequestContentType(requestContentFormat)
						.withResponseContentType(responseContentFormat);
					
					String[] invocationDetails = src.getRequirements().split("\\s*;\\s*");
					Execution execution = new Execution()
						.withInvocationDetail(Arrays.asList(invocationDetails));
					
					return new Conversion()
							.withMapping(mapping)
							.withExecution(execution);					
				}).collect(Collectors.toList());
	}

}
