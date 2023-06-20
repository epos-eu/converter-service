package org.epos.converter.app.router;

import static org.epos.router_framework.domain.BuiltInActorType.CONVERTER;

import org.epos.router_framework.Router;
import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.exception.RoutingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Service;

@Service
public class RouterInitializer {
	
	private static final Logger LOG = LoggerFactory.getLogger(RouterInitializer.class);	

	public void initRouter(Router router) throws BeanInitializationException
	{
		try {
			router.init(System.getenv("BROKER_HOST"),
					System.getenv("BROKER_VHOST"),
					System.getenv("BROKER_USERNAME"),
					System.getenv("BROKER_PASSWORD"));
		} catch (RoutingException e) {
			String errMsg = String.format("%s instance for '%s' component failed to initialise",
								router.getClass().getSimpleName(), Actor.getInstance(CONVERTER).name());
			LOG.error(errMsg);
    		throw new BeanInitializationException(errMsg, e);
		}
	}

}
