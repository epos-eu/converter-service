package org.epos.converter.app.configuration.properties;

import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.types.ServiceType;

/**
 * Holds all externalized properties for <b>Routing-Framework</b> instances, used in the configuration of service-level routing .
 */
public class RoutingFrameworkProperties {
	
	private ServiceType serviceType;
	private Actor defaultNextComponent;
	private int numOfConsumers;
	private int numOfPublishers;
	
	public ServiceType getServiceType() {
		return serviceType;
	}
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	public Actor getDefaultNextComponent() {
		return defaultNextComponent;
	}
	public void setDefaultNextComponent(Actor defaultNextComponent) {
		this.defaultNextComponent = defaultNextComponent;
	}
	public int getNumOfConsumers() {
		return numOfConsumers;
	}
	public void setNumOfConsumers(int numOfConsumers) {
		this.numOfConsumers = numOfConsumers;
	}
	public int getNumOfPublishers() {
		return numOfPublishers;
	}
	public void setNumOfPublishers(int numOfPublishers) {
		this.numOfPublishers = numOfPublishers;
	}
	
}
