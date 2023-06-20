package org.epos.converter.app.plugin.proxy.impl;

import org.epos.converter.app.plugin.proxy.PluginProxy;
import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;

/**
 * @author patk
 * 
 * If there are any additional deployment details required for this type of plug-in (e.g. connection
 *  details to another server) then this would be place where they would get loaded.
 * 
 * Plug-ins for this implementation would require the classes of {@link org.epos.converter.common.java}
 *  to made serialisable, as they may need to be loaded both by the Converter's classloader and the plug-in's
 *  classloader.
 *  I'm not sure whether, with RMI, it would work with the client and server code being run on the same JVM. 
 *  
 *
 */
public class JavaRMIPluginProxy extends PluginProxy {

	public JavaRMIPluginProxy(ConversionDescriptor conversion)
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
