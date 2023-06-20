package org.epos.converter.app.configuration.properties;

import java.nio.file.Path;

import org.epos.converter.app.plugin.managment.PluginsMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("plugins.config")
public class PluginsConfigProperties {
	
	private final Path pluginsInstallationLocation;
	private final PluginsMode pluginsMode;
	private final Path pluginsRootCloneLocation;
	
	public PluginsConfigProperties(Path pluginsInstallationLocation, String pluginsMode, Path pluginsRootCloneLocation) 
	{
		this.pluginsInstallationLocation = pluginsInstallationLocation;
		this.pluginsMode = PluginsMode.valueOf(pluginsMode);
		this.pluginsRootCloneLocation = pluginsRootCloneLocation;
	}

	public Path getPluginsInstallationLocation() {
		return pluginsInstallationLocation;
	}

	public PluginsMode getPluginsMode() {
		return pluginsMode;
	}

	public Path getPluginsRootCloneLocation() {
		return pluginsRootCloneLocation;
	}

}
