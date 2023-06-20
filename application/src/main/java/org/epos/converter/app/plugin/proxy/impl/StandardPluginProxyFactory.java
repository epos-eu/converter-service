package org.epos.converter.app.plugin.proxy.impl;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.epos.converter.app.plugin.proxy.PluginProxy;
import org.epos.converter.app.plugin.proxy.PluginProxyFactory;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.springframework.stereotype.Service;

import static org.epos.converter.common.plugin.type.PluginProxyType.*;
import static org.epos.converter.common.util.LambdaExceptionUtil.rethrowFunction;

@Service
public final class StandardPluginProxyFactory implements PluginProxyFactory {
	
	private final Map<PluginProxyType, Function<ConversionDescriptor, PluginProxy>> invokerSuppliers = new EnumMap<>(PluginProxyType.class);
	
	public StandardPluginProxyFactory() {
		invokerSuppliers.putAll(Map.of(
			JAVA_REFLECTION, rethrowFunction(JavaReflectionPluginProxy::new),
			JAVA_RMI, rethrowFunction(JavaRMIPluginProxy::new),
			JAVA_JINI, rethrowFunction(JavaJINIPluginProxy::new),
			PYTHON, rethrowFunction(PythonPluginProxy::new),
			GO, rethrowFunction(GoPluginProxy::new)
		));
	}

	@Override
	public PluginProxy instance(PluginProxyType invokerType, ConversionDescriptor conversionDescriptor) throws PluginConfigurationException
	{
		Function<ConversionDescriptor, PluginProxy> constructorFunc = invokerSuppliers.get(invokerType);
		return constructorFunc.apply(conversionDescriptor);
	}

}
