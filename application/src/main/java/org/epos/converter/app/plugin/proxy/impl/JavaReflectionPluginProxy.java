package org.epos.converter.app.plugin.proxy.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.epos.converter.app.plugin.core.PluginFirstClassLoader;
import org.epos.converter.app.plugin.proxy.PluginProxy;
import org.epos.converter.common.java.CallableJavaPlugin;
import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author patk
 * 
 * Simplest method of invocation: invocation to plug-in running within the same JVM 
 * Each plug-in invoked using own child-first classLoader.
 *
 */
public class JavaReflectionPluginProxy extends PluginProxy {
	
	private static Logger LOG = LoggerFactory.getLogger(JavaReflectionPluginProxy.class);

	public JavaReflectionPluginProxy(ConversionDescriptor conversion) throws PluginConfigurationException
	{
		super(conversion);
		instance = initConversionInstance(conversion);

		String processName = String.format("%s:%s", 
				conversion.getExecutionDescriptor().getProxyType(), 
				conversion.getMappingDescriptor().getStatementOfUniqueness());

		ClassLoader cl = instance.getClass().getClassLoader();

		executor = Executors.newSingleThreadExecutor(r -> {
			Thread th = new Thread(r, processName);
			th.setContextClassLoader(cl);
			return th;
		});
	}

	private static final int TARGET_DETAIL_SIZE = 3;

	private CallableJavaPlugin instance;
	private ExecutorService executor;

	@SuppressWarnings("unchecked")
	private CallableJavaPlugin initConversionInstance(ConversionDescriptor conversion) throws PluginConfigurationException {

		Class<? extends CallableJavaPlugin> clazz;
		Constructor<? extends CallableJavaPlugin> targetConstructor;

		ExecutionDescriptor execution = conversion.getExecutionDescriptor();
		Path targetJarPath = Paths.get(
									execution.getPluginInstallLoc(),
									execution.getTargetDetail()[0],	// directory containing target jar 
									execution.getTargetDetail()[1]	// target jar name
								   ).normalize();
		String targetJarLocation = targetJarPath.normalize().toString();
		String targetClassName = execution.getTargetDetail()[2];

		try {		
			URL[] jarUrls = new URL[] { new URL("jar:file:" + targetJarLocation + "!/") };
			LOG.debug("Classloader urls: {}", Arrays.toString(jarUrls));
	
			final ClassLoader cl = new PluginFirstClassLoader(jarUrls, JavaReflectionPluginProxy.class.getClassLoader());
			clazz = (Class<? extends CallableJavaPlugin>) Class.forName(targetClassName, true, cl);
			targetConstructor = clazz.getDeclaredConstructor(MappingDescriptor.class);

		} catch (MalformedURLException e) {
			throw new PluginConfigurationException(
				String.format("Proxy for conversion could not be initialised: "
						+ "Jar Location (%s) could not be established", targetJarLocation), 
				e);
		} catch (ClassNotFoundException | NoClassDefFoundError e) { 
			throw new PluginConfigurationException(
				String.format("Proxy for conversion could not be initialised: "
						+ "Could not locate target Java class '%s' from location '%s' [%s: %s]%n"
						+ "Has the plug-in been deployed to the correct location?",
					targetClassName, targetJarLocation, e.getClass().getSimpleName(), e.getMessage()));
		} catch (NoSuchMethodException | SecurityException e) {
			throw new PluginConfigurationException(
				String.format("Proxy for conversion could not be initialised: "
						+ "Could not locate constructor for the target Java class '%s'", 
					targetClassName), 
				e);
		}

		try {
			return targetConstructor.newInstance(conversion.getMappingDescriptor());
		} catch (InstantiationException e) {
			throw new PluginConfigurationException(
				String.format("Proxy for conversion could not be initialised: "
						+ "Constructor not accessible for the target Java class '%s'", targetClassName), 
				e);
		} catch (IllegalAccessException e) {
			throw new PluginConfigurationException(
				String.format("Proxy for conversion could not be initialised: "
						+ "Target Java class '%s' not instantiable", targetClassName), 
				e);
		} catch (IllegalArgumentException e) {
			throw new PluginConfigurationException(
				String.format("Proxy for conversion could not be initialised: "
						+ "Illegal argument was passed to the constructor of the target Java class '%s'", targetClassName),
				e);
		} catch (InvocationTargetException e) {
			throw new PluginConfigurationException( 
				String.format("Proxy for conversion could not be initialised: "
						+ "Constructor for the target Java class '%s' could not be invoked", targetClassName),
				e);
		}
	}

	@Override
	public String invoke(String payload) throws PayloadMappingException {

		Future<String> result = executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return instance.invoke(payload);
			}
		});

		try {
			return result.get();
		} catch (InterruptedException | ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof PayloadMappingException) {
				throw (PayloadMappingException)cause;
			} else {	
				String errStr = String.format("Conversion failure: %s", e.getMessage());
				LOG.error(errStr);
				throw new PayloadMappingException(errStr, e);
			}
		}
	}

	@Override
	protected void doValidateExecutionDetail(ExecutionDescriptor execution) throws PluginConfigurationException 
	{
		// basic check of TargetDetail array content
		String[] targetDetail = execution.getTargetDetail();

		if (targetDetail == null || Arrays.stream(targetDetail)
				.filter(Objects::nonNull).filter(s -> !s.isEmpty())
				.count() < TARGET_DETAIL_SIZE) {

			String errMsg = String.format("%s plugin, does not contain sufficient detail "
					+ "(i.e. name of plugin jar and implementation class) for it to be invoked", 
					execution.getProxyType().toString());
			throw new PluginConfigurationException(errMsg);
		}
	}

}
