package org.epos.converter.app.msghandling.router.configuration;

import static org.epos.router_framework.domain.BuiltInActorType.CONVERTER;
import static org.epos.router_framework.domain.BuiltInActorType.DB_CONNECTOR;
import static org.epos.router_framework.domain.BuiltInActorType.TCS_CONNECTOR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.epos.converter.app.configuration.properties.RoutingFrameworkProperties;
import org.epos.converter.app.router.RouterInitializer;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.router_framework.RelayRouter;
import org.epos.router_framework.RelayRouterBuilder;
import org.epos.router_framework.Router;
import org.epos.router_framework.RpcRouter;
import org.epos.router_framework.RpcRouterBuilder;
import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.domain.BuiltInActorType;
import org.epos.router_framework.domain.Request;
import org.epos.router_framework.domain.Response;
import org.epos.router_framework.exception.RoutingException;
import org.epos.router_framework.handling.PlainTextRelayRouterHandlerFactory;
import org.epos.router_framework.types.PayloadType;
import org.epos.router_framework.types.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Configuration
//@EnableConfigurationProperties(RoutingFrameworkProperties.class)
public class MessageHandlingConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageHandlingConfiguration.class);	
	private static final Actor THIS_COMPONENT = Actor.getInstance(CONVERTER);
	
	private final RouterInitializer routerInitializer;
	
	@Autowired
	public MessageHandlingConfiguration(RouterInitializer routerInitializer) {			
		this.routerInitializer = routerInitializer;
	}

	@Bean
	@ConfigurationProperties("conversion.messaging.routing-framework.relay")	
	public RoutingFrameworkProperties conversionMessageRoutingProperties(){
		return new RoutingFrameworkProperties();
	}
	
	@Bean
    public RelayRouter plainTextRelayRouter(PlainTextRelayRouterHandlerFactory plainTextHandlerFactory,
    		RoutingFrameworkProperties conversionMessageRoutingProperties)
    {
		ServiceType serviceType = conversionMessageRoutingProperties.getServiceType();
		Actor defaultNextComponent = conversionMessageRoutingProperties.getDefaultNextComponent();
		int numOfConsumers = conversionMessageRoutingProperties.getNumOfConsumers();
		int numOfProducers = conversionMessageRoutingProperties.getNumOfPublishers();
		
		Optional<RelayRouter> router = RelayRouterBuilder.instance(THIS_COMPONENT)
				.addServiceType(serviceType, defaultNextComponent)
				.addPlainTextPayloadTypeSupport(plainTextHandlerFactory)
				.setNumberOfConsumers(numOfConsumers)
				.setNumberOfPublishers(numOfProducers)
				.build();

         return router.orElseThrow(() -> 
        		new BeanInitializationException(
        				String.format("Router instance for %s component could not be instantiated", THIS_COMPONENT.name()))
        		);
    }
	

	@Bean
	@Profile({ "prod", "dev" })
	public CommandLineRunner messageHandlingCommandLineRunner(ApplicationContext ctx, RelayRouter plainTextRelayRouter) 
	{
		return args -> {
			
			// Initialisation of router for handling Conversion and Plug-in Management requests
			routerInitializer.initRouter(plainTextRelayRouter);

	 		if (LOG.isInfoEnabled()) {
	 			String infoMsgFmt = "Router initialisation completed for Message Handling ['%s' component]: Ready to communicate with message bus.";
	 			LOG.info(String.format(infoMsgFmt, THIS_COMPONENT.name()));
	 		}
		};
	}
	
	@Bean
	@Profile({ "test" })
	public CommandLineRunner mockMessageHandlingCommandLineRunner() 
	{
		return args -> {			
			/* No initialisation of router required for handling Conversion and Plug-in Management 
			 * requests when in test mode.
			 */
	 		LOG.info(String.format(
	 				"No router initialisation for Message Handling required when in test mode ['%s' component].", 
	 				THIS_COMPONENT.name()));
		};
	}
    
}
