package org.epos.converter.app.msghandling.conversion;

import org.epos.converter.app.plugin.managment.ConversionMetadata;

public final class ConversionMessage {
	
	private ConversionMetadata metadata;
	private String content;
	
	public ConversionMessage(String content, ConversionMetadata metadata) {
		this.content = content;
		this.metadata = metadata;
	}

	public String getContent() {
		return content;
	}

	public ConversionMetadata getMetadata() {
		return metadata;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
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
		ConversionMessage other = (ConversionMessage) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		return true;
	}

}
