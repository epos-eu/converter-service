package org.epos.converter.app.plugin.proxy.impl;

import org.epos.converter.app.plugin.proxy.PluginProxy;
import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.HeaderDescriptor;

/**
 * @author patk
 * 
 * Placeholder implementation!
 * 
 * If there are any additional deployment details required for this type of plug-in (e.g. connection
 *  details to another server) then this would be place where they would get loaded.
 *  
 */
public class JavaJINIPluginProxy extends PluginProxy {

	public JavaJINIPluginProxy(ConversionDescriptor conversion)
			throws PluginConfigurationException {
		super(conversion);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String invoke(String payload) throws PayloadMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doValidateExecutionDetail(ExecutionDescriptor execution) throws PluginConfigurationException {
		// TODO Auto-generated method stub
	}


	
}
