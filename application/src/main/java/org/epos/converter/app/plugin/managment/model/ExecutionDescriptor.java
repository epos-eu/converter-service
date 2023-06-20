package org.epos.converter.app.plugin.managment.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "invocationDetail"
})
public final class ExecutionDescriptor {
	
	private final String[] invocationDetail;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public ExecutionDescriptor(@JsonProperty("invocationDetail") String[] invocationDetail) {
		this.invocationDetail = invocationDetail;
	}
	
	public static ExecutionDescriptor newInstance(ExecutionDescriptor executionDescriptor) {
		String[] invocationDetail = executionDescriptor.getInvocationDetail().clone();
		return new ExecutionDescriptor(invocationDetail);
	}

	public String[] getInvocationDetail() {
		return invocationDetail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(invocationDetail);
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
		ExecutionDescriptor other = (ExecutionDescriptor) obj;
		if (!Arrays.equals(invocationDetail, other.invocationDetail))
			return false;
		return true;
	}

}
