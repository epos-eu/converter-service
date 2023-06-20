package org.epos.converter.app.msghandling.router;

import java.io.Serializable;
import java.util.Map;

import org.epos.converter.app.msghandling.conversion.ConversionMessage;
import org.epos.converter.app.msghandling.conversion.ConversionMessageHandler;
import org.epos.converter.app.msghandling.exception.HandlingConfigurationException;
import org.epos.converter.app.msghandling.exception.MessageProcessingException;
import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.exception.RoutingMessageHandlingException;
import org.epos.router_framework.handling.PlainTextRelayRouterHandler;
import org.epos.router_framework.types.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles incoming messages requiring conversion.
 *  
 * These messages originate from the system's message broker and the converted messages are returned to the message broker for 
 * further routing.
 * 
 */
public class DefaultRoutingHandler extends PlainTextRelayRouterHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRoutingHandler.class);

	private ConversionMessageHandler conversionHandler;
	
	public DefaultRoutingHandler(Actor defaultNextActor, ConversionMessageHandler conversionHandler) {
		super(defaultNextActor);
		this.conversionHandler = conversionHandler;
	}
	
	@Override
	public Serializable handle(String payload, ServiceType service, Map<String, Object> headers) throws RoutingMessageHandlingException 
	{
		// Conversion messages: payloads requiring conversion  
		if (ServiceType.EXTERNAL.equals(service)) {
			try {
				ConversionMessage reqMsg = PlainTextConversionMessageTransformer.transformFromExternalRepr(payload);				
				ConversionMessage respMsgObj = conversionHandler.handle(reqMsg);
				return PlainTextConversionMessageTransformer.transformFromInternalRepr(respMsgObj);
			} catch (HandlingConfigurationException | MessageProcessingException e) {
				throw new RoutingMessageHandlingException(e.getMessage(), e);
			}
		} else {
			String errMsg = String.format("ServiceType, '%s', not supported by component", service.getServiceLabel()); 
			LOG.error(errMsg);
			throw new RoutingMessageHandlingException(errMsg);
		}
	}
		
}
