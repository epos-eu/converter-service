package org.epos.converter.app.plugin.managment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonPropertyOrder({
    "mapping",
    "execution"
})
@JsonDeserialize(builder = ConversionDescriptor.Builder.class)
public final class ConversionDescriptor {
	
	private final MappingDescriptor mapping;
	private final ExecutionDescriptor execution;

	public static ConversionDescriptor newInstance(ConversionDescriptor conversionDescriptor) {
		return new ConversionDescriptor(conversionDescriptor.getMapping(), conversionDescriptor.getExecution());
	}
	
	private ConversionDescriptor(MappingDescriptor mapping, ExecutionDescriptor execution) {
		this.mapping = mapping;
		this.execution = execution;
	}
	
	public MappingDescriptor getMapping() {
		return mapping;
	}
	public ExecutionDescriptor getExecution() {
		return ExecutionDescriptor.newInstance(execution);
	}
	
	@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
	public static class Builder {
		
		private final MappingDescriptor mapping;
		private final ExecutionDescriptor execution;
//		private MappingSchemasDescriptor mappingSchemas;
		
		public Builder(
				@JsonProperty("mapping") MappingDescriptor mapping,
				@JsonProperty("execution") ExecutionDescriptor execution) {
			this.mapping = mapping;
			this.execution = execution;
		}
		
//		@JsonProperty("mappingSchemas")
//		Builder withMappingSchemas(MappingSchemasDescriptor mappingSchemas) {
//			this.mappingSchemas = mappingSchemas;
//			return this;
//		}

		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		public ConversionDescriptor build() {
			return new ConversionDescriptor(mapping, execution);
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((execution == null) ? 0 : execution.hashCode());
		result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConversionDescriptor other = (ConversionDescriptor) obj;
		if (execution == null) {
			if (other.execution != null)
				return false;
		} else if (!execution.equals(other.execution))
			return false;
		if (mapping == null) {
			if (other.mapping != null)
				return false;
		} else if (!mapping.equals(other.mapping))
			return false;
		return true;
	}
	
}
