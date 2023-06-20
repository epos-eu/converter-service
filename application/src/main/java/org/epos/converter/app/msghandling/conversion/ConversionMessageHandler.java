package org.epos.converter.app.msghandling.conversion;

import org.epos.converter.app.msghandling.exception.HandlingConfigurationException;
import org.epos.converter.app.msghandling.exception.MessageProcessingException;
import org.epos.converter.app.plugin.managment.ConversionMetadata;
import org.epos.converter.app.plugin.managment.PluginsMetadataCacheAdapter;
import org.epos.converter.app.plugin.proxy.PluginProxy;
import org.epos.converter.app.plugin.proxy.PluginProxyLocator;
import org.epos.converter.common.exception.UnsupportedPayloadConversionException;
import org.epos.converter.common.payload.validation.PayloadValidator;
import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConversionMessageHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConversionMessageHandler.class);
	
	private PluginsMetadataCacheAdapter pluginMetadataAccessor;
	private PluginProxyLocator pluginProxyLocator;

	public ConversionMessageHandler(PluginsMetadataCacheAdapter pluginMetadataAccessor, PluginProxyLocator pluginProxyLocator) 
	{
		this.pluginMetadataAccessor = pluginMetadataAccessor;
		this.pluginProxyLocator = pluginProxyLocator;
	}
	
	public ConversionMessage handle(ConversionMessage requestMessage) throws MessageProcessingException, HandlingConfigurationException
	{		
		ConversionMetadata reqMetadata = requestMessage.getMetadata();
		String reqContent = requestMessage.getContent();
		
		// lookup conversion from plugins metadata cache
		ConversionDescriptor convDescriptor = pluginMetadataAccessor.findBestMatchConversion(reqMetadata).orElseThrow(() -> 
				new MessageProcessingException(String.format(
						"No conversion available for incoming message with the metadata, [%s]", 
						reqMetadata)));		
		ConversionSchemasDescriptor convContentSchemaDescriptor = pluginMetadataAccessor.findContentSchemasDescriptor(convDescriptor)
				.orElse(null);

		try {
			PayloadValidator validator = getPayloadValidator(convDescriptor, convContentSchemaDescriptor);
			
			// validate incoming message content (if validation schema provided)
			valdiateIncomingMsgContent(validator, reqContent);
			
			// invoke payload conversion
			PluginProxy invoker = pluginProxyLocator.locate(convDescriptor);
			LOG.debug("Attempting message conversion using the descriptor, {}", convDescriptor);
			String convertedContent = invoker.invoke(reqContent);
			LOG.debug("Completed message conversion using the descriptor, {}", convDescriptor);
			
			// validate converted message content (if validation schema provided)
			validateConvertedMsgContent(validator, convertedContent);

			// construct response message
			ConversionMetadata updatedConvMetadata = transformToConversionMetadata(convDescriptor);			
			return new ConversionMessage(convertedContent, updatedConvMetadata);
			
		} catch (UnsupportedPayloadConversionException | ContentValidationException e) {
			// exceptions that indicate an issue with the conversion message request
			String warnMsg = String.format("Failure to convert message payload: %s", e.getMessage());
			LOG.warn(warnMsg);
			throw new MessageProcessingException(warnMsg, e);	
		} catch (PluginConfigurationException | PayloadMappingException e) {
			// exceptions that indicate an issue with the plug-in's implementation or configuration
			String errMsg = String.format("Unexpected error during message payload conversion: %s", e.getMessage());
			LOG.error(errMsg);
			throw new HandlingConfigurationException(errMsg, e);
		}
		
	}

	private void validateConvertedMsgContent(PayloadValidator validator, String content) throws PayloadMappingException 
	{
		if (validator != null) {			
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format(
						"Attempting schema validation for converted message with content...%n%s%n", content));
			}
			try {
				validator.validateResponsePayload(content);				
			} catch (ContentValidationException e) {
				throw new PayloadMappingException("Response payload failed vaildation", e); 
			}
		}
	}

	private void valdiateIncomingMsgContent(PayloadValidator validator, String content) throws ContentValidationException 
	{
		if (validator != null) {			
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format(
						"Attempting schema validation for incoming message with content...%n%s%n", content));
			}
			validator.validateRequestPayload(content);
		}
	}

	private PayloadValidator getPayloadValidator(ConversionDescriptor convDescriptor, ConversionSchemasDescriptor convSchemaDescriptor) 
			throws UnsupportedPayloadConversionException, PluginConfigurationException 
	{
		if (convSchemaDescriptor != null) {
			return new PayloadValidator(convDescriptor, convSchemaDescriptor, new DefaultContentOperationSetFactory());
		}
		return null;
	}

	private ConversionMetadata transformToConversionMetadata(ConversionDescriptor convDescriptor) 
	{
		String pluginId = convDescriptor.getExecutionDescriptor().getParentPluginId();
		String pluginVersion = convDescriptor.getExecutionDescriptor().getParentPluginVersion();
		String requestType = convDescriptor.getMappingDescriptor().getRequestType();
		ContentType requestContentType = convDescriptor.getMappingDescriptor().getRequestContentType();
		ContentType responseContentType = convDescriptor.getMappingDescriptor().getResponseContentType();
		
		return new ConversionMetadata.Builder(requestType)
				.withConversionRequestContentType(requestContentType)
				.withConversionResponseContentType(responseContentType)
				.withPluginId(pluginId)
				.withPluginVersion(pluginVersion)
				.build();
	}
}
