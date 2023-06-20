package org.epos.converter.app.plugin.managment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "id",
    "version"
})
public final class PluginKey {
	
	private final String id;
	private final String version;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public PluginKey(
			@JsonProperty("id") String id, 
			@JsonProperty("version") String version) {
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}
	public String getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		PluginKey other = (PluginKey) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + " (v" + version + ")";
	}
	
}
