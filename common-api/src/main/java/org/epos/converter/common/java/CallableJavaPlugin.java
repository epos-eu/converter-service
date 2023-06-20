package org.epos.converter.common.java;

import static java.util.stream.Collectors.toMap;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.epos.converter.common.exception.PayloadProcessingException;
import org.epos.converter.common.plugin.descriptor.ConversionsDescriptorReader;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.schema.validation.ContentOperationSetFactory;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.epos.converter.common.type.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CallableJavaPlugin {
	
	private static Logger LOG = LoggerFactory.getLogger(CallableJavaPlugin.class);	
	private static final ContentOperationSetFactory contentOperationSetFactory = new DefaultContentOperationSetFactory();
	private static final String DEFAULT_CONVERSIONS_DESCRIPTOR_FILENAME = "conversions-descriptor.json";	
	
	protected final MappingDescriptor mapping;	
	private final Set<MappingDescriptor> supportedMappings;
	private final Map<ContentFormat, ContentOperationSet<?>> payloadOperationSets;
	
	protected abstract Optional<String> doInvoke(String payload) throws PayloadProcessingException;
		
	protected CallableJavaPlugin(MappingDescriptor mapping) throws PluginConfigurationException {

		String descriptorLoc = System.getProperties().containsKey("EPOS-PLUGIN-DESCRIPTOR-LOCATION") ?
									System.getProperty("EPOS-PLUGIN-DESCRIPTOR-LOCATION") : 
									DEFAULT_CONVERSIONS_DESCRIPTOR_FILENAME;

		Map<String, List<MappingDescriptor>> conversions = new ConversionsDescriptorReader(
													Paths.get(descriptorLoc),
													getClass().getClassLoader()
												).parse();
		List<MappingDescriptor> mappings = conversions.get(this.getClass().getName());
		supportedMappings = Set.copyOf(mappings);			
		
		if (!supportedMappings.contains(mapping)) {
			String errMsg = String.format("The conversions implementaion '%s' does not support the attempted mapping%n"
					+ "  -> %s%nSupported mappings are...%n  -> %s", 
					getClass().getName(), mapping.toString(), 
					getSupportedMappings().stream()
						.map(MappingDescriptor::getStatementOfUniqueness)
						.collect(Collectors.joining("; ", "[", "]")));
			throw new PluginConfigurationException(errMsg);
		}
		
		// set support for operations on the payloads		
		payloadOperationSets = supportedMappings.stream()
			.flatMap(c -> Stream.of(c.getRequestContentType(), c.getResponseContentType()))
			.map(ContentType::getContentFormat)
			.distinct()
			.collect(toMap(Function.identity(),f -> contentOperationSetFactory.getInstance(f)));
		
		this.mapping = mapping;
	}

	protected MappingDescriptor getMapping() {
		return mapping;
	}
	
	protected final Set<MappingDescriptor> getSupportedMappings() throws PluginConfigurationException {
		return supportedMappings;
	}
	
	public String invoke(String payload) throws PayloadProcessingException 
	{
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("incoming content...%n>>>%n%s%n<<<%n", payload));			
		}
		String convertedPayload = doInvoke(payload).orElse("");
		
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("outgoing content...%n>>>%n%s%n<<<%n", convertedPayload));			
		}		
		return convertedPayload;
	}
	
	protected ContentOperationSet<?> getPayloadOperationSet(ContentFormat payloadFormat) {
		return payloadOperationSets.get(payloadFormat);
	}

}
