package org.epos.converter.common.plugin.type;

import java.util.Optional;

import org.epos.converter.common.type.ContentType;

public final class MappingDescriptor {
	
	private String requestType;
	private ContentType requestContentType;
	private ContentType responseContentType;
	private Optional<String[]> attributes;
	
	public MappingDescriptor(String requestType, ContentType requestContentType, ContentType responseContentType) 
	{
		this(requestType, requestContentType, responseContentType, Optional.empty());
	}
	
	public MappingDescriptor(String requestType, ContentType requestContentType, ContentType responseContentType, Optional<String[]> attributes) 
	{
		super();
		this.requestType = requestType;
		this.requestContentType = requestContentType;
		this.responseContentType = responseContentType;
		this.attributes = attributes;
	}
	
	public Optional<String[]> getAttributes() {
		return attributes;
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
	
	public String getStatementOfUniqueness() {
		
		if (attributes.isPresent() && attributes.get().length > 0) {
			return String.format("%s|%s|%s|%s", requestType, 
					requestContentType.name(), responseContentType.name(),
					String.join("-", attributes.get()));
		} else {
			return String.format("%s|%s|%s", requestType, 
					requestContentType.name(), responseContentType.name());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
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
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
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

	@Override
	public String toString() {
		return "MappingDescriptor [requestType=" + requestType + ", requestContentType=" + requestContentType
				+ ", responseContentType=" + responseContentType + ", attributes=" + attributes + "]";
	}

}
