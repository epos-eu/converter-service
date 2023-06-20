package org.epos.converter.app.plugin.managment.model;

import org.epos.converter.common.type.ContentType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonPropertyOrder({
    "requestType",
    "requestContentType",
    "responseContentType"
})
@JsonDeserialize(builder = MappingDescriptor.Builder.class)
public final class MappingDescriptor {
	
	private final String requestType;
	private final ContentType requestContentType;
	private final ContentType responseContentType;
	
	private MappingDescriptor(String requestType, ContentType requestContentType, ContentType responseContentType) {
		this.requestType = requestType;
		this.requestContentType = requestContentType;
		this.responseContentType = responseContentType;
	}
	
	public String getRequestType() {
		return requestType;
	}
	public ContentType getRequestContentType() {
		return requestContentType;
	}
	public ContentType getResponseContentType() {
		return responseContentType;
	}
	
	@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
	public static class Builder {
		
		private final String requestType;		
		private ContentType requestContentType;
		private ContentType responseContentType;
		
		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		public Builder(@JsonProperty("requestType") String requestType) {
			this.requestType = requestType;
		}
		
		@JsonProperty("requestContentType")
		public Builder withRequestContentType(String requestContentType) {			
			this.requestContentType = ContentType.fromValue(requestContentType).orElseThrow();
			return this;
		}
		
		@JsonProperty("responseContentType")
		public Builder withResponseContentType(String responseContentType) {
			this.responseContentType = ContentType.fromValue(responseContentType).orElseThrow();
			return this;
		}
		
		public MappingDescriptor build() {
			return new MappingDescriptor(requestType, requestContentType, responseContentType);
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requestContentType == null) ? 0 : requestContentType.hashCode());
		result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
		result = prime * result + ((responseContentType == null) ? 0 : responseContentType.hashCode());
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
		MappingDescriptor other = (MappingDescriptor) obj;
		if (requestContentType != other.requestContentType)
			return false;
		if (requestType == null) {
			if (other.requestType != null)
				return false;
		} else if (!requestType.equals(other.requestType))
			return false;
		if (responseContentType != other.responseContentType)
			return false;
		return true;
	}
	
}
