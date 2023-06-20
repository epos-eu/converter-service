package org.epos.converter.app.plugin.proxy;

import static org.epos.converter.common.util.LambdaExceptionUtil.rethrowFunction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Used to retrieve appropriate {@link PluginProxy} implementations based on a provided {@link ConversionDescriptor}.
 * Will lazily instantiate {@link PluginProxy} implementations and cache them. Implementations are determined by consulting the Plug-in Registry to determine the correct {@link PluginProxy} to return. 
 *
 */
@Component
public class PluginProxyLocator {
	
	private final PluginProxyFactory pluginProxyFactory;
	
	@Autowired
	public PluginProxyLocator(PluginProxyFactory pluginProxyFactory) {
		this.pluginProxyFactory = pluginProxyFactory;
	}

	final Map<ExecutionDescriptor, Map<MappingDescriptor, PluginProxy>> cache = new ConcurrentHashMap<>();
	
	/**
	 * Currently the cached entries are not implemented to expire  
	 * 
	 * @param pluginDetail
	 * @param conversionDescriptor 
	 * @return
	 */
	public PluginProxy locate(ConversionDescriptor conversionDescriptor) throws PluginConfigurationException
	{
		MappingDescriptor mappingDescriptor = conversionDescriptor.getMappingDescriptor();
		ExecutionDescriptor executionDescriptor = conversionDescriptor.getExecutionDescriptor();

		Map<MappingDescriptor, PluginProxy> conversionCache = cache.computeIfAbsent(executionDescriptor, 
				k -> new ConcurrentHashMap<MappingDescriptor, PluginProxy>());
		PluginProxyType invokerType = executionDescriptor.getProxyType();
		
		return conversionCache.computeIfAbsent(mappingDescriptor, 
				rethrowFunction(k -> pluginProxyFactory.instance(invokerType, conversionDescriptor)));
	}

}
