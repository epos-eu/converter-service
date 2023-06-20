package org.epos.converter.app.plugin.managment.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DO representing a detailed description of a plugin (i.e. plugin metadata and the conversions it supports)
 *
 */
@JsonPropertyOrder({
    "pluginHeader",
    "conversions"
})
public final class PluginDescriptor {
	
	private final PluginKey key;
	private final PluginHeaderDescriptor header;
	private final List<ConversionDescriptor> conversions;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public PluginDescriptor(
			@JsonProperty("pluginKey") PluginKey key,
			@JsonProperty("pluginHeader") PluginHeaderDescriptor header,
			@JsonProperty("conversions") List<ConversionDescriptor> conversions) {
		this.key = key;
		this.header = header;
		this.conversions = conversions;
	}

	public PluginKey getKey() {
		return key;
	}
	public PluginHeaderDescriptor getHeader() {
		return header;
	}
	public List<ConversionDescriptor> getConversions() {
		return conversions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conversions == null) ? 0 : conversions.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		PluginDescriptor other = (PluginDescriptor) obj;
		if (conversions == null) {
			if (other.conversions != null)
				return false;
		} else if (!conversions.equals(other.conversions))
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
}
