package org.epos.converter.app.msghandling.router;

import org.epos.converter.app.msghandling.conversion.ConversionMessageHandler;
import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.handling.PlainTextRelayRouterHandler;
import org.epos.router_framework.handling.PlainTextRelayRouterHandlerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoutingHandlerFactory implements PlainTextRelayRouterHandlerFactory {
	
	private ConversionMessageHandler conversionHandler;

	public DefaultRoutingHandlerFactory(ConversionMessageHandler conversionHandler) {
		this.conversionHandler = conversionHandler;
	}

	@Override
	@Cacheable
	public PlainTextRelayRouterHandler getRelayRouterHandler(Actor defaultNextActor) 
	{
		return new DefaultRoutingHandler(defaultNextActor, conversionHandler);
	}

}
