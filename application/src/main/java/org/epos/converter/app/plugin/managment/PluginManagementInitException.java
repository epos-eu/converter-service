package org.epos.converter.app.plugin.managment;

/**
 * Represents failure to initialise some aspect of the plug-in management 
 */
public class PluginManagementInitException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PluginManagementInitException(String msg, Exception e) {
		super(msg, e);
	}
}
