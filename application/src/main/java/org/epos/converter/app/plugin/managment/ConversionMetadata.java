package org.epos.converter.app.plugin.managment;

import java.util.Optional;

import org.epos.converter.common.type.ContentType;

public class ConversionMetadata {
	
	private String pluginId;
	private String pluginVersion;
	private String conversionRequestType;
	private ContentType conversionRequestContentType;
	private ContentType conversionResponseContentType;
	
	private ConversionMetadata(String pluginId, String pluginVersion, String conversionRequestType, 
			ContentType conversionRequestContentType, ContentType conversionResponseContentType) 
	{
		this.pluginId = pluginId;
		this.pluginVersion = pluginVersion;
		this.conversionRequestType = conversionRequestType;
		this.conversionRequestContentType = conversionRequestContentType;
		this.conversionResponseContentType = conversionResponseContentType;
	}

	public String getConversionRequestType() {
		return conversionRequestType;
	}
	public Optional<String> getPluginId() {
		return Optional.ofNullable(pluginId);
	}
	public Optional<String> getPluginVersion() {
		return Optional.ofNullable(pluginVersion);
	}
	public Optional<ContentType> getConversionRequestContentType() {
		return Optional.ofNullable(conversionRequestContentType);
	}
	public Optional<ContentType> getConversionResponseContentType() {
		return Optional.ofNullable(conversionResponseContentType);
	}
	
	public static class Builder {
		private final String conversionRequestType;
		private ContentType conversionRequestContentType;
		private ContentType conversionResponseContentType;
		private String pluginId;
		private String pluginVersion;
		
		public Builder(String conversionRequestType) {
			this.conversionRequestType = conversionRequestType;
		}
		
		public Builder withConversionRequestContentType(ContentType conversionRequestContentType) {
			this.conversionRequestContentType = conversionRequestContentType;
			return this;
		}
		
		public Builder withConversionResponseContentType(ContentType conversionResponseContentType) {
			this.conversionResponseContentType = conversionResponseContentType;
			return this;
		}
		
		public Builder withPluginId(String pluginId) {
			this.pluginId = pluginId;
			return this;
		}
		
		public Builder withPluginVersion(String pluginVersion) {
			this.pluginVersion = pluginVersion;
			return this;
		}
		
		public ConversionMetadata build() {
			return new ConversionMetadata(pluginId, pluginVersion, 
					conversionRequestType,
					conversionRequestContentType, conversionResponseContentType);
		}
		
	}

	@Override
	public String toString() {
		return "ConversionMetadata [pluginId=" + pluginId + ", pluginVersion=" + pluginVersion
				+ ", conversionRequestType=" + conversionRequestType + ", conversionRequestContentType="
				+ conversionRequestContentType + ", conversionResponseContentType=" + conversionResponseContentType
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conversionRequestContentType == null) ? 0 : conversionRequestContentType.hashCode());
		result = prime * result + ((conversionRequestType == null) ? 0 : conversionRequestType.hashCode());
		result = prime * result
				+ ((conversionResponseContentType == null) ? 0 : conversionResponseContentType.hashCode());
		result = prime * result + ((pluginId == null) ? 0 : pluginId.hashCode());
		result = prime * result + ((pluginVersion == null) ? 0 : pluginVersion.hashCode());
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
		ConversionMetadata other = (ConversionMetadata) obj;
		if (conversionRequestContentType != other.conversionRequestContentType)
			return false;
		if (conversionRequestType == null) {
			if (other.conversionRequestType != null)
				return false;
		} else if (!conversionRequestType.equals(other.conversionRequestType))
			return false;
		if (conversionResponseContentType != other.conversionResponseContentType)
			return false;
		if (pluginId == null) {
			if (other.pluginId != null)
				return false;
		} else if (!pluginId.equals(other.pluginId))
			return false;
		if (pluginVersion == null) {
			if (other.pluginVersion != null)
				return false;
		} else if (!pluginVersion.equals(other.pluginVersion))
			return false;
		return true;
	}


}
