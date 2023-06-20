package org.epos.converter.app.plugin.managment;

import java.nio.file.Path;

import org.epos.converter.app.plugin.managment.exception.PluginInstallationException;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;

public interface PluginsFileManager {
	
	boolean isPluginInstalled(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor);
	
	Path getPluginInstallLocation(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor);
	
	void install(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor) throws PluginInstallationException;
	
	void install(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor, String accessToken) throws PluginInstallationException;

	void uninstall(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor);

}
