package org.epos.converter.app.plugin.managment;

import java.util.Set;

import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;

public interface PluginDescriptorsReader {
	
	Set<PluginDescriptor> parse(String content) throws PluginConfigurationException;

}
