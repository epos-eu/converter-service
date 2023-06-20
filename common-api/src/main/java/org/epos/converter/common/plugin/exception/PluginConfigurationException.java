package org.epos.converter.common.plugin.exception;

/**
 * Typically indicating a deployment issue relating to the plug-in's declared detail
 *
 */
public class PluginConfigurationException extends Exception {

	private static final long serialVersionUID = -8580369808253402736L;
	
	public PluginConfigurationException(String errMsg) {
		super(errMsg);
	}
	
	public PluginConfigurationException(String errMsg, Exception e) {
		super(errMsg, e);
	}
	
}
