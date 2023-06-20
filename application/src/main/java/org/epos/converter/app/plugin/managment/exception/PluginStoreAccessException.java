package org.epos.converter.app.plugin.managment.exception;

/**
 * Represents failure to access the external plug-ins store 
 */
public class PluginStoreAccessException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public PluginStoreAccessException(String message) {
		super(message);
	}

	public PluginStoreAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
