package org.epos.converter.common.payload.validation;

import java.util.Objects;

import org.epos.converter.common.exception.UnsupportedPayloadConversionException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;
import org.epos.converter.common.plugin.type.HeaderDescriptor;
import org.epos.converter.common.plugin.type.PluginDescriptor;
import org.epos.converter.common.schema.validation.ContentOperationSetFactory;
import org.epos.converter.common.schema.validation.ContentValidator;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;

/**
 * Instances of this are used to validate incoming/outgoing payloads for a given conversion.
 */
public class PayloadValidator {
	
	private ContentValidator requestValidator;
	private ContentValidator responseValidator;
		
	public PayloadValidator(ConversionDescriptor conversionDescriptor, ConversionSchemasDescriptor conversionSchemasDescriptor,
			ContentOperationSetFactory contentOperationSetFactory) throws UnsupportedPayloadConversionException, PluginConfigurationException
	{
		Objects.requireNonNull(conversionDescriptor);
		Objects.requireNonNull(conversionSchemasDescriptor);
		Objects.requireNonNull(contentOperationSetFactory);
		
		try {
			requestValidator = new ContentValidator(conversionSchemasDescriptor.getRequestRootSchema());
			responseValidator = new ContentValidator(conversionSchemasDescriptor.getResponseRootSchema());
		} catch (ConfigurationException e) {
			throw new PluginConfigurationException("Failed to create request/response payload validators", e);
		}
	}
	
	public PayloadValidator(ConversionDescriptor conversionDescriptor, PluginDescriptor pluginDescriptor, 
			ContentOperationSetFactory contentOperationSetFactory) throws UnsupportedPayloadConversionException, PluginConfigurationException {	
		
		this(conversionDescriptor, pluginDescriptor.getConversions().get(conversionDescriptor), contentOperationSetFactory);
		
//		Objects.requireNonNull(conversionDescriptor);
//		Objects.requireNonNull(pluginDescriptor);
//		Objects.requireNonNull(contentOperationSetFactory);
		
//		ConversionSchemasDescriptor conversionSchemasDescriptor = pluginDescriptor.getConversions().get(conversionDescriptor);
//		
//		if (conversionSchemasDescriptor == null) {
//			HeaderDescriptor pluginHeader = pluginDescriptor.getHeader();		
//			throw new UnsupportedPayloadConversionException(
//					String.format("The plugin '%s' with ID '%s' does not support the specified Conversion, '%s'", 
//					pluginHeader.getName(), pluginHeader.getId(), conversionDescriptor.toString()));
//		}

		
//		try {
//			requestValidator = new ContentValidator(schemas.getRequestRootSchema());
//			responseValidator = new ContentValidator(schemas.getResponseRootSchema());
//		} catch (ConfigurationException e) {
//			throw new PluginConfigurationException("Failed to create payload validators", e);
//		}

	}

	public void validateRequestPayload(String payload) throws ContentValidationException {
		requestValidator.validateContent(payload);
	}
	
	public void validateResponsePayload(String payload) throws ContentValidationException {
		responseValidator.validateContent(payload);
	}
}
