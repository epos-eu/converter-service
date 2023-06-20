package org.epos.converter.app.plugin.proxy;

import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;

public abstract class PluginProxy {
	
	protected final ExecutionDescriptor execution;

	protected PluginProxy(ConversionDescriptor conversionDescriptor) throws PluginConfigurationException {			
		execution = validateExecutionDetail(conversionDescriptor.getExecutionDescriptor());
	}

	public abstract String invoke(String payload) throws PayloadMappingException;
	
	protected abstract void doValidateExecutionDetail(ExecutionDescriptor execution) throws PluginConfigurationException;
	
	protected ExecutionDescriptor validateExecutionDetail(ExecutionDescriptor executionDescriptor) throws PluginConfigurationException 
	{
		boolean hasMandatoryFields = 
				executionDescriptor != null && 
				executionDescriptor.getProxyType() != null &&
				executionDescriptor.getTargetDetail() != null;
				
		if (!hasMandatoryFields) {			
			String errMsg = String.format("Execution description is missing one of the mandatory fields:%n "
					+ "Proxy Type; Target Detail");
			throw new PluginConfigurationException(errMsg);
		}
		
		doValidateExecutionDetail(executionDescriptor);
		return executionDescriptor;
	}


}
