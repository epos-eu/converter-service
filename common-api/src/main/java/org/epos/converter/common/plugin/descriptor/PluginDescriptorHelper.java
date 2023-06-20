package org.epos.converter.common.plugin.descriptor;

import java.util.Map;
import java.util.Optional;

import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.HeaderDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.plugin.type.PluginDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentType;
import org.epos.converter.common.type.LocationType;

/**
 * Helps with creation of Plug-in Descriptor beans as used by the invoker and payload validation code.
 * Current subclasses include...
 * <ul>
 * <li>{@link PluginDescriptorFromJsonHelper} used when reading configuration from a json file
 * <li>{@link PluginDescriptorFromCfgHelper} used when creating configuration from plugin store beans
 *
 * @param <T> Type representing the plugin descriptor
 * @param <U> Type representing the conversion descriptor
 */
public abstract class PluginDescriptorHelper<T, U> {
	
	public PluginDescriptor createPluginDescriptor(T inType) throws ConfigurationException {
		HeaderDescriptor header = createHeader(inType);
		Map<ConversionDescriptor, ConversionSchemasDescriptor> conversions = createConversions(inType);
		
		return new PluginDescriptor(conversions, header);
	}
	
	public abstract ConversionDescriptor createConversionDescriptor(U inType) throws PluginConfigurationException;
	
	protected abstract Map<ConversionDescriptor, ConversionSchemasDescriptor> createConversions(T inType);

	protected abstract HeaderDescriptor createHeader(T inType);

	protected ConversionDescriptor createConversionDescriptor(ExecutionDescriptor executionDescriptor, MappingDescriptor mappingDescriptor) {		
		return new ConversionDescriptor(executionDescriptor, mappingDescriptor);
	}
	
	protected MappingDescriptor createMappingDescriptor(String requestType, String requestContentType, String responseContentType, String... attributes) throws PluginConfigurationException {

		ContentType _requestContentType = ContentType.fromValue(requestContentType).orElseThrow(() -> {
			String errStr = String.format("Media Type value, '%s', is not supported.%n "
				+ "Available IDs: %s", requestContentType, ContentType.prettyPrintSupportedValues());
			return new PluginConfigurationException(errStr);
		});

		ContentType _responseContentType = ContentType.fromValue(responseContentType).orElseThrow(() -> {
			String errStr = String.format("Media Type value, '%s', is not supported.%n "
				+ "Available IDs: %s", requestContentType, ContentType.prettyPrintSupportedValues());
			return new PluginConfigurationException(errStr);
		});

		Optional<String[]> _attributes = (attributes == null || attributes.length == 0) ? 
											Optional.empty() : Optional.of(attributes);

		return new MappingDescriptor(requestType, _requestContentType, _responseContentType, _attributes);
	}
	
	protected ExecutionDescriptor createExecutionDescriptor(String invokerType, String pluginInstallLoc, String targetDetail) {
		
		PluginProxyType proxyType = PluginProxyType.getInstance(invokerType).orElseThrow(() -> {
			String errMsg = String.format("Proxy Type ID, '%s', is not supported.%n "
					+ "Available IDs: %s", 
					invokerType, PluginProxyType.prettyPrintSupportedIds());
			return new IllegalArgumentException(errMsg);
		});
		
		String[] targetDetails = targetDetail.trim().split("\\s*;\\s*");
		
		return new ExecutionDescriptor(proxyType, pluginInstallLoc, targetDetails);
	}
	
	protected SchemaDescriptorTree createSchemaRoot(ContentFormat pointerContentFormat, String schemaName, String schemaLocation, String schemaLocationType) throws PluginConfigurationException {
		
		LocationType _schemaLocationType = LocationType.getInstance(schemaLocationType).orElseThrow(() -> {
			String errStr = String.format("Location Type, '%s', is not supported.", schemaLocationType);
			return new PluginConfigurationException(errStr);
		});

		ContentFormat contentFormat = pointerContentFormat;
		try {			
			SchemaDescriptor schemaDescriptor = new SchemaDescriptor(pointerContentFormat, contentFormat, schemaName, _schemaLocationType, schemaLocation);
			return SchemaDescriptorTreeBuilder.instance(schemaDescriptor)
				.build(new DefaultContentOperationSetFactory());
		} catch (IllegalArgumentException e) {
			String errMsg = String.format("Failed to create schema descriptor due inconsistencies in the plug-in descriptor: %s", e.getMessage());
			// TODO log errMsg @ERROR level
			throw new PluginConfigurationException(errMsg, e);
		}
	}

}
