package org.epos.converter.common.exception;

import org.epos.converter.common.plugin.exception.PluginConfigurationException;

/**
 * Indicates a conversion is not currently supported by the Converter component instance.
 * 
 * The blame for this is assumed to be with the incoming conversion message request rather than the configuration of
 * of the plug-ins maintained by the Converter (c.f. {@link PluginConfigurationException})  
 */
public class UnsupportedPayloadConversionException extends Exception {

	private static final long serialVersionUID = -8852173672593000379L;

	public UnsupportedPayloadConversionException(String message) {
		super(message);
	}

	public UnsupportedPayloadConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedPayloadConversionException(Throwable cause) {
		super(cause);
	}

}
