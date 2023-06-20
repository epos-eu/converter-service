package org.epos.converter.app.configuration.properties;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Optional;

import org.epos.router_framework.types.ServiceType;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ServiceTypeConverter implements Converter<String, ServiceType> {
	
	private static final String PRETTY_BUILTIN_SERVICETYPE_VALUES = Arrays.stream(ServiceType.values())
			.map(ServiceType::getServiceLabel)
			.collect(joining(", "));

	@Override
	public ServiceType convert(String source) 	
	{
		Optional<ServiceType> serviceType = ServiceType.getInstance(source);
		if (serviceType.isEmpty()) {
			String errStr = String.format("Attempted to match property value '%s' to an ServiceType instance but no match found. "
					+ "Available values are [%s]", 
					source, PRETTY_BUILTIN_SERVICETYPE_VALUES);
			throw new IllegalArgumentException(errStr);
		}		
		return serviceType.get();
	}

}
