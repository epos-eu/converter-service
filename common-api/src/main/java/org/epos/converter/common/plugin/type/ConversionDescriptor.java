package org.epos.converter.common.plugin.type;

public class ConversionDescriptor {
	
	private ExecutionDescriptor executionDescriptor;
	private MappingDescriptor mappingDescriptor;
	
	public ConversionDescriptor(ExecutionDescriptor executionDescriptor, MappingDescriptor mappingDescriptor) 
	{
		this.executionDescriptor = executionDescriptor;
		this.mappingDescriptor = mappingDescriptor;
	}

	public ExecutionDescriptor getExecutionDescriptor() {
		return executionDescriptor;
	}

	public MappingDescriptor getMappingDescriptor() {
		return mappingDescriptor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((executionDescriptor == null) ? 0 : executionDescriptor.hashCode());
		result = prime * result + ((mappingDescriptor == null) ? 0 : mappingDescriptor.hashCode());
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
		if (executionDescriptor == null) {
			if (other.executionDescriptor != null)
				return false;
		} else if (!executionDescriptor.equals(other.executionDescriptor))
			return false;
		if (mappingDescriptor == null) {
			if (other.mappingDescriptor != null)
				return false;
		} else if (!mappingDescriptor.equals(other.mappingDescriptor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConversionDescriptor [executionDescriptor=" + executionDescriptor + ", mappingDescriptor="
				+ mappingDescriptor + "]";
	}

}
