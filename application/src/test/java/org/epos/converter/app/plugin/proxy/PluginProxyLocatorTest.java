package org.epos.converter.app.plugin.proxy;

import static org.epos.converter.common.type.ContentType.APPLICATION_JSON;
import static org.epos.converter.common.type.ContentType.APPLICATION_XML;
import static org.epos.converter.common.type.ContentType.EPOS_GEO_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the caching abilities of the {@link PluginProxyLocator}
 *
 */
class PluginProxyLocatorTest {
	
	private PluginProxyLocator pluginProxyLocator;

	@BeforeEach
	void setUp() throws Exception 
	{
		pluginProxyLocator = new PluginProxyLocator(new PluginProxyFactory() {

			@Override
			public PluginProxy instance(PluginProxyType invokerType, ConversionDescriptor conversionDescriptor) {
				
				PluginProxy fakeProxy = null; 
				
				try {
					fakeProxy = new PluginProxy(conversionDescriptor) {
	
						@Override
						public String invoke(String payload) throws PayloadMappingException {
							return null;
						}
	
						@Override
						protected void doValidateExecutionDetail(ExecutionDescriptor execution)
								throws PluginConfigurationException {
							// just do nothing
						}
						
					};				
				} catch (PluginConfigurationException e) {
					e.printStackTrace();
				}
				
				return fakeProxy;
			}

		});
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	private String getPluginProjectLocation(String targetPluginProjectname) 
	{
		return System.getProperty("user.dir") + "/../" + targetPluginProjectname;
	}
	
	@Test
	@DisplayName("Locating and caching of PluginProxy")
	void testLocateJavaReflectionPluginProxy() 
	{
		final String pluginArtefactRelativePath = "target";
		
		// EXECUTIONS
		ExecutionDescriptor execution_1 = new ExecutionDescriptor(
				"TCS/VisualiseStation/Plugin",
				PluginProxyType.JAVA_REFLECTION,
				getPluginProjectLocation("plugin-visualise-station"),
				pluginArtefactRelativePath,
				"plugin-visualise-station-TEST.jar",
				"org.epos.converter.plugin.invokertest.InvokerTestCallablePlugin");
		
		//   different callable class
		ExecutionDescriptor execution_2 = new ExecutionDescriptor(
				"TCS/VisualiseStation/Plugin",
				PluginProxyType.JAVA_REFLECTION,
				getPluginProjectLocation("plugin-visualise-station"),
				pluginArtefactRelativePath,
				"plugin-visualise-station-TEST.jar",
				"org.epos_ip.converter.plugin.invokertest.InvokerTestCallable2Plugin");

		//   different plug-in/location
		ExecutionDescriptor execution_3 = new ExecutionDescriptor(
				"TCS/VisualiseStation/Plugin",
				PluginProxyType.JAVA_REFLECTION,
				getPluginProjectLocation("plugin-visualise-station2"),
				pluginArtefactRelativePath,
				"plugin-visualise-station-2",
				"org.epos.converter.plugin.visualisestation.VisualiseStationXMLCallablePlugin");
	
		//   different proxy type
		ExecutionDescriptor execution_4 = new ExecutionDescriptor(
				"SomeRMI/Plugin",
				PluginProxyType.JAVA_RMI,
				getPluginProjectLocation("some-rmi-plugin"),
				"could be something very important here!");
		
		// MAPPINGS
		MappingDescriptor mapping_1 = new MappingDescriptor("visualise/station/wp10-json", APPLICATION_JSON, EPOS_GEO_JSON);
		//   different request type
		MappingDescriptor mapping_2 = new MappingDescriptor("visualise/station/fsdn-xml", APPLICATION_XML, EPOS_GEO_JSON);
		//   different response type
		MappingDescriptor mapping_3 = new MappingDescriptor("OTHER_TRANSFORMATION_TYPE-1", APPLICATION_JSON, APPLICATION_JSON);
		
		int expectedTopLevelCacheSize = 0;
		int expectedChildCacheSize_1 = 0;
		int expectedChildCacheSize_2 = 0;
		int expectedChildCacheSize_3 = 0;
		int expectedChildCacheSize_4 = 0;
		
		try {
			
			// #1 Cache PluginProxy for NEW EXECUTION (#1), NEW MAPPING (#1)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_1, mapping_1));		
			assertEquals(++expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(++expectedChildCacheSize_1, pluginProxyLocator.cache.get(execution_1).size());
			
			// #2 Cache PluginProxy for DIFFERENT EXECUTION (#4), DIFFERENT MAPPING (#1)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_4, mapping_1));
			assertEquals(++expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(++expectedChildCacheSize_2, pluginProxyLocator.cache.get(execution_4).size());
			
			// #3 Cache PluginProxy for DIFFERENT EXECUTION (#3), DIFFERENT MAPPING (#2)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_3, mapping_2));
			assertEquals(++expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(++expectedChildCacheSize_3, pluginProxyLocator.cache.get(execution_3).size());
			
			// #4 Cache PluginProxy for SAME EXECUTION (#1), DIFFERENT MAPPING (#2)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_1, mapping_2));
			assertEquals(expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(++expectedChildCacheSize_2, pluginProxyLocator.cache.get(execution_1).size());
			
			// #5 Cache PluginProxy for SAME EXECUTION (#1), DIFFERENT MAPPING (#3)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_1, mapping_3));
			assertEquals(expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(++expectedChildCacheSize_2, pluginProxyLocator.cache.get(execution_1).size());
			
			// #5 Cache PluginProxy for SAME EXECUTION (#1), SAME MAPPING (#3)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_1, mapping_3));
			assertEquals(expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(expectedChildCacheSize_2, pluginProxyLocator.cache.get(execution_1).size());
			
			// #6 Cache PluginProxy for DIFFERENT EXECUTION (#2), DIFFERENT MAPPING (#1)
			pluginProxyLocator.locate(new ConversionDescriptor(execution_2, mapping_1));
			assertEquals(++expectedTopLevelCacheSize, pluginProxyLocator.cache.size());
			assertEquals(++expectedChildCacheSize_4, pluginProxyLocator.cache.get(execution_2).size());

		} catch (PluginConfigurationException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
