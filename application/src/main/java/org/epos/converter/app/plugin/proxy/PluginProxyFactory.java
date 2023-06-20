package org.epos.converter.app.plugin.proxy;

import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;

public interface PluginProxyFactory {
	
	PluginProxy instance(PluginProxyType invokerType, ConversionDescriptor conversionDescriptor) throws PluginConfigurationException;
	
}
