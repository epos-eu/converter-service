package org.epos.converter.app.configuration.properties;

import java.util.Arrays;
import java.util.Objects;

import org.epos.converter.common.type.ContentType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:pluginsmetadata-plugin_ICSC_V1.yml", factory = YamlPropertySourceFactory.class)
@ConstructorBinding
@ConfigurationProperties("plugins.metadata.bootstrap.plugin")
public final class PluginsMetadataBootstrapPluginProperties {
	
	private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.APPLICATION_JSON;
	
	private final String id;
	private final String version;
	private final String[] installedArtifactsRelativeLocations;
	private final String defaultRequestType;
	private final ContentType requestContentType;
	private final ContentType responseContentType;
	private final String[] defaultInvocationDetail;
	private final String requestSchema;
	
	public PluginsMetadataBootstrapPluginProperties(String id, String version,
			String[] installedArtifactsRelativeLocations,
			String defaultRequestType, String requestContentType, String responseContentType,
			String[] defaultInvocationDetail, String requestSchema) 
	{
		this.id = Objects.requireNonNull(id);
		this.version = Objects.requireNonNull(version);
		this.installedArtifactsRelativeLocations = Objects.requireNonNull(installedArtifactsRelativeLocations);
		this.defaultRequestType = Objects.requireNonNull(defaultRequestType);
		this.requestContentType = ContentType.fromValue(requestContentType).orElse(DEFAULT_CONTENT_TYPE);
		this.responseContentType = ContentType.fromValue(responseContentType).orElse(DEFAULT_CONTENT_TYPE);
		this.defaultInvocationDetail = Objects.requireNonNull(defaultInvocationDetail);
		this.requestSchema = requestSchema;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}
	
	public String[] getInstalledArtifactsRelativeLocations() {
		return Arrays.copyOf(installedArtifactsRelativeLocations, 
				installedArtifactsRelativeLocations.length);
	}
	
	public String getDefaultRequestType() {
		return defaultRequestType;
	}

	public String[] getDefaultInvocationDetail() {
		return Arrays.copyOf(defaultInvocationDetail, 
				defaultInvocationDetail.length);
	}

	public String getRequestSchema() {
		return requestSchema;
	}

	public static ContentType getDefaultContentType() {
		return DEFAULT_CONTENT_TYPE;
	}

	public ContentType getRequestContentType() {
		return requestContentType;
	}

	public ContentType getResponseContentType() {
		return responseContentType;
	}

}
