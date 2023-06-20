package org.epos.converter.app.plugin.managment.model;

/**
 * Represents the plugin's various status' on the Converter instance it is installed on.
 *
 */
public class PluginStatus {
	
	// Indicates whether the artifacts for the plugin have been installed.
	private boolean installed;
	
	// Indicated whether the plugin has been enabled for use
	private boolean enabled;
	
	public PluginStatus() {
		installed = false;
		enabled = false;
	}

	public boolean isInstalled() {
		return installed;
	}
	
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) 
	{
		if (enabled && !installed) {
			String errFmt = "Value cannot be '%b' if the plugin artifacts have not been installed.";
			throw new IllegalArgumentException(String.format(errFmt, enabled));
		}
		this.enabled = enabled;
	}
	
}
