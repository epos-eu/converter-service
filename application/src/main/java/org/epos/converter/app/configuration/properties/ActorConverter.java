package org.epos.converter.app.configuration.properties;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Optional;

import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.domain.BuiltInActorType;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ActorConverter implements Converter<String, Actor> {
	
	private static final String PRETTY_BUILTIN_ACTOR_VALUES = Arrays.stream(BuiltInActorType.values())
				.map(BuiltInActorType::verbLabel)
				.collect(joining(", "));

	@Override
	public Actor convert(String source) 
	{		
		// ensure specified Actor value corresponds to a compile-time supported entity (i.e. BuiltInActorType)
		Optional<BuiltInActorType> builtInActor = BuiltInActorType.instance(source);
		if (builtInActor.isEmpty()) {
			String errStr = String.format("Attempted to match property value '%s' to an Actor instance but no match found. "
					+ "Available values are [%s]", 
					source, PRETTY_BUILTIN_ACTOR_VALUES);
			throw new IllegalArgumentException(errStr);
		}		
		return Actor.getInstance(builtInActor.get());
	}

}
