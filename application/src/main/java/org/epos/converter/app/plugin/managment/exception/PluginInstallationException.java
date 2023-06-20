package org.epos.converter.app.plugin.managment.exception;

/**
 * Represents a failure to install a plugin onto a Converter instance
 */
public class PluginInstallationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public PluginInstallationException(String message) {
		super(message);
	}
	
	public PluginInstallationException(String message, Throwable cause) {
		super(message, cause);
	}

}
