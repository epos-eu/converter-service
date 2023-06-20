package org.epos.converter.app.plugin.managment;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.configuration.properties.PluginsConfigProperties;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Provides a view onto the Plugins Metadata cache that can be used by the Conversion message handling code
 *
 */
@Component
@EnableConfigurationProperties(PluginsConfigProperties.class)
public class PluginsMetadataCacheAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(PluginsMetadataCacheAdapter.class);
	
	private static final String ICSC_PLUGINS_DEFAULT_REQUESTTYPE = "icsc-default";
	
	private PluginsMetadataCache pluginMetadataCache;
	private Path pluginsInstallLoc;
	private PluginsMode pluginMode;
	
	@Autowired
	public PluginsMetadataCacheAdapter(PluginsMetadataCache pluginMetadataCache, PluginsConfigProperties pluginsConfigProps) 
	{
		//PluginsMetadataCache pluginMetadataCache, Path pluginsInstallLoc  PluginsMode pluginMode
		this.pluginMetadataCache = pluginMetadataCache;
		this.pluginsInstallLoc = pluginsConfigProps.getPluginsInstallationLocation();
		this.pluginMode = pluginsConfigProps.getPluginsMode();
	}
	
	public Optional<ConversionDescriptor> findBestMatchConversion(ConversionMetadata searcher) 
	{
		var conversions = pluginMetadataCache.findConversions(searcher);		
		int numCandidateConversions = conversions.size();
		if (numCandidateConversions > 0) {
			
			var conv = conversions.iterator().next();
			if (numCandidateConversions > 1 && LOG.isWarnEnabled()) {
				LOG.warn(String.format(
						"Unique conversion match for the incoming message could not be determined " +
						"(%d candidate conversions identified)%n" +
						"  Picked best match: %s", numCandidateConversions, conv.toString()));
			}
			
			PluginKey pluginKey = conv.getRight();
			PluginHeaderDescriptor pluginHeaderDescriptor = pluginMetadataCache.findHeaderDescriptor(pluginKey);
			
			var commonModelConvDescriptor = transformToCommonModel(conv.getLeft(), pluginKey, pluginHeaderDescriptor);
			return Optional.of(commonModelConvDescriptor);
		} 
		return Optional.empty();		
	}

	public Optional<ConversionSchemasDescriptor> findContentSchemasDescriptor(ConversionDescriptor searcher) {
		var convManagementModel = transformToManagementModel(searcher);
		return pluginMetadataCache.findContentSchemasDescriptor(convManagementModel);
	}
	
	private org.epos.converter.common.plugin.type.ConversionDescriptor transformToCommonModel(
			org.epos.converter.app.plugin.managment.model.ConversionDescriptor convDescriptor, 
			PluginKey pluginKey, PluginHeaderDescriptor pluginHeaderDescriptor) 
	{
		// common MappingDescriptor
		org.epos.converter.app.plugin.managment.model.MappingDescriptor pluginManagmentMappingDescriptor = 
				convDescriptor.getMapping();
		
		String requestType;
		
		switch(pluginMode) {
			case FLEXIBILE:
				requestType = pluginManagmentMappingDescriptor.getRequestType();
				break;
			case ICSC_V1:
				requestType = ICSC_PLUGINS_DEFAULT_REQUESTTYPE;
				break;
			default: 
				requestType = null;
		}
				
		MappingDescriptor commonMappingDescriptor = new MappingDescriptor(requestType,
				pluginManagmentMappingDescriptor.getRequestContentType(),
				pluginManagmentMappingDescriptor.getResponseContentType());
		
		// common ExecutionDescriptor
		org.epos.converter.app.plugin.managment.model.ExecutionDescriptor pluginManagmentExecutionDescriptor = 
				convDescriptor.getExecution();
		
		Path pluginsInstallAbsLoc = pluginsInstallLoc.resolve(pluginHeaderDescriptor.getInstallLocation());
		
		ExecutionDescriptor commonExecutionDescriptor = new ExecutionDescriptor(
				pluginKey.getId(), pluginKey.getVersion(),
				pluginHeaderDescriptor.getProxyType(),
				pluginsInstallAbsLoc.toString(),
				pluginManagmentExecutionDescriptor.getInvocationDetail());
		
		return new org.epos.converter.common.plugin.type.ConversionDescriptor(commonExecutionDescriptor, commonMappingDescriptor);
	}
	
	private Pair<org.epos.converter.app.plugin.managment.model.ConversionDescriptor, PluginKey> transformToManagementModel(
			ConversionDescriptor searcher) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
