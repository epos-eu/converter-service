package org.epos.converter.app.plugin.managment.model;

import java.net.URL;
import java.util.List;

import org.epos.converter.common.plugin.type.PluginProxyType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonPropertyOrder({
    "name",
    "description",
    "proxyType",
    "repoLocation",
    "repoArtifactLocations",
    "installLocation",
    "authorName",
    "authorContact"
})
@JsonDeserialize(builder = PluginHeaderDescriptor.Builder.class)
public final class PluginHeaderDescriptor {
	
	private final String name;
	private final String authorName;
	private final String authorContact;
	private final String description;
	private final URL repoLocation;
	private final List<String> repoArtifactLocations;
	private final Boolean repoAccessRestriction;
	private final String installLocation;
	private final PluginProxyType proxyType;
	
	private PluginHeaderDescriptor(String installLocation, PluginProxyType proxyType, List<String> repoArtifactLocations,
			URL repoLocation, Boolean repoAccessRestriction, 
			String name, String authorName, String authorContact, String description) 
	{
		this.installLocation = installLocation;
		this.proxyType = proxyType;
		this.repoLocation = repoLocation;
		this.repoArtifactLocations = repoArtifactLocations;
		this.repoAccessRestriction = repoAccessRestriction;
		this.name = name;
		this.authorName = authorName;
		this.authorContact = authorContact;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public String getAuthorName() {
		return authorName;
	}
	public String getAuthorContact() {
		return authorContact;
	}
	public String getDescription() {
		return description;
	}
	public URL getRepoLocation() {
		return repoLocation;
	}
	public String getInstallLocation() {
		return installLocation;
	}
	public List<String> getRepoArtifactLocations() {
		return repoArtifactLocations;
	}
	public Boolean isRepoAccessRestriction() {
		return repoAccessRestriction;
	}
	public PluginProxyType getProxyType() {
		return proxyType;
	}

	@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
	public static class Builder {
		
		// Mandatory plugin details
		private final String installLocation;
		private final PluginProxyType proxyType;
		private final List<String> repoArtifactLocations;
		// Non-mandatory plugin details
		private String name;
		private String authorName;
		private String authorContact;
		private String description;
		// Installing details
		private URL repoLocation;
		private Boolean repoAccessRestriction;
		
		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		public Builder(
				@JsonProperty("installLocation") String installLocation,
				@JsonProperty("proxyType") String proxyType,
				@JsonProperty("repoArtifactLocations") List<String> repoArtifactLocations)
		{
			this.installLocation = installLocation;
			this.proxyType = PluginProxyType.getInstance(proxyType).orElseThrow();
			this.repoArtifactLocations = repoArtifactLocations;
		}
		
		@JsonProperty("repoLocation")
		public Builder withRepoLocation(URL repoLocation) {
			this.repoLocation = repoLocation;
			return this;
		}
		
		@JsonProperty("repoAccessRestriction")
		public Builder withRepoAccessRestriction(Boolean repoAccessRestriction) {
			this.repoAccessRestriction = repoAccessRestriction;
			return this;
		}

		@JsonProperty("name")
		public Builder withName(String name) {
			this.name = name;
			return this;
		}
		
		@JsonProperty("authorName")
		public Builder withAuthorName(String authorName) {
			this.authorName = authorName;
			return this;
		}
		
		@JsonProperty("authorContact")
		public Builder withAuthorContact(String authorContact) {
			this.authorContact = authorContact;
			return this;
		}
		
		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}
		
		public PluginHeaderDescriptor build() {
			return new PluginHeaderDescriptor(installLocation, proxyType, repoArtifactLocations, 
					repoLocation, repoAccessRestriction, name, authorName, authorContact, description);
		}
		
	}


}
