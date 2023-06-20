package org.epos.converter.app.plugin.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import org.epos.converter.app.plugin.proxy.impl.StandardPluginProxyFactory;
import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.ContentType;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InvokeTest_JavaReflectionPluginIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeTest_JavaReflectionPluginIT.class);
	
	private PluginProxyLocator pluginProxyLocator;
	
	@BeforeEach
	void setUp() throws Exception 
	{
		pluginProxyLocator = new PluginProxyLocator(new StandardPluginProxyFactory());
	}
	
	@Test
	void testLocateJavaReflectionPluginProxy_OK() {

		MappingDescriptor conversion = new MappingDescriptor("OTHER_TRANSFORMATION_TYPE-1", 
				ContentType.APPLICATION_JSON, ContentType.APPLICATION_JSON);
		
		final String targetPluginProjectname = "plugin-invoker-test";
		final String pluginProjectLocation = System.getProperty("user.dir") + "/../" + targetPluginProjectname;
		final String pluginArtefactRelativePath = "target";
		final String targetPluginLocation = Paths.get(pluginProjectLocation, pluginArtefactRelativePath).toString();
		final String targetPluginClassname = "org.epos.converter.plugin.invokertest.InvokerTestCallablePlugin";
		
		ExecutionDescriptor execution = new ExecutionDescriptor(
				"PLUGIN_INVOKER_TEST",
				PluginProxyType.JAVA_REFLECTION,
				pluginProjectLocation,
				pluginArtefactRelativePath,
				TestUtils.findNameOfTargetJar(targetPluginLocation),
				targetPluginClassname);
	
		try {
			PluginProxy locate = pluginProxyLocator.locate(new ConversionDescriptor(execution, conversion));
			assertNotNull(locate);
		} catch (PluginConfigurationException e) {
			LOGGER.error("testLocateJavaReflectionPluginProxy_OK",e);
			fail();
		}
	}
	
	@Test
	@DisplayName("Validation on JavaReflection")
	void testLocateJavaReflectionPluginProxy_NOK_missing_execution_details() {

		MappingDescriptor mapping = new MappingDescriptor("OTHER_TRANSFORMATION_TYPE-1", 
				ContentType.APPLICATION_JSON, ContentType.APPLICATION_JSON);
		
		final String targetPluginProjectname = "plugin-invoker-test";
		final String pluginProjectLocation = System.getProperty("user.dir") + "/../" + targetPluginProjectname;
		final String pluginArtefactRelativePath = "target";
		final String targetPluginClassname = "org.epos.converter.plugin.invokertest.InvokerTestCallablePlugin";
		
		ExecutionDescriptor execution = new ExecutionDescriptor(
				"PLUGIN_INVOKER_TEST",
				PluginProxyType.JAVA_REFLECTION,
				pluginProjectLocation,
				pluginArtefactRelativePath,
				// TestUtils.findNameOfTargetJar(Paths.get(pluginProjectLocation, pluginArtefactRelativePath).toString()),
				targetPluginClassname
			);

		try {
			PluginProxy proxy = pluginProxyLocator.locate(new ConversionDescriptor(execution, mapping));
			assertNotNull(proxy);
			fail();
		} catch (PluginConfigurationException e) {
			assertTrue(e.getCause() == null);
		}
	}
	
	@Test
	@DisplayName("Isolated Classloading for Converter Plugins")
	void testInvoker_pluginSupported() 
	{
		MappingDescriptor mapping = new MappingDescriptor("OTHER_TRANSFORMATION_TYPE-1", 
				ContentType.APPLICATION_JSON, ContentType.APPLICATION_JSON);
		
		final String targetPluginProjectname = "plugin-invoker-test";
		final String pluginProjectLocation = System.getProperty("user.dir") + "/../" + targetPluginProjectname;
		final String pluginArtefactRelativePath = "target";
		
		final String targetPluginLocation = Paths.get(pluginProjectLocation, pluginArtefactRelativePath).toString();		
		final String targetPluginJarName = TestUtils.findNameOfTargetJar(targetPluginLocation);
		
		final String targetPluginClassname = "org.epos.converter.plugin.invokertest.InvokerTestCallablePlugin";
		
		ExecutionDescriptor execution = new ExecutionDescriptor(
				"PLUGIN_INVOKER_TEST",
				PluginProxyType.JAVA_REFLECTION,
				pluginProjectLocation,
				pluginArtefactRelativePath,
				targetPluginJarName,
				targetPluginClassname
			);
		
		ConversionDescriptor conversion = new ConversionDescriptor(execution, mapping);
		
		try {
			// force loading of a common org.epos.converter.common.util.LambdaExceptionUtil class
			LambdaExceptionUtil lambdaExceptionUtil = new LambdaExceptionUtil();
			String classLoaderClazz = lambdaExceptionUtil.getClass().getClassLoader().getClass().getName();

			String outputFromLocalClassInstance = lambdaExceptionUtil.toString();
			LOGGER.info("Caller: {} | {} ({})", lambdaExceptionUtil.getClass().getName(), classLoaderClazz, outputFromLocalClassInstance);
			
			PluginProxy invoker = pluginProxyLocator.locate(conversion);
			
			String incomingPayload = "{\"my_payload\":\"<<< INCOMING PAYLOAD >>>\"}";
			String outgoingPayload = invoker.invoke(incomingPayload);
			
			String expected = String.format(
					"{\"in-payload\":\"%s\",\"mapping\":\"%s\",\"local-class\":\"%s\"}",
					incomingPayload.replaceAll("[{}\"]", ""), 
					mapping.getStatementOfUniqueness(),
					"PLUGIN IMPLEMENTATION OF org.epos.converter.common.util.LambdaExceptionUtil");			
			assertEquals(expected, outgoingPayload);			
			
		} catch (PayloadMappingException | PluginConfigurationException e) {
			LOGGER.error("testInvoker_pluginSupported",e);
			Assertions.fail();
		}
	}

}
