package org.epos.converter.app.plugin.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.plugin.proxy.impl.StandardPluginProxyFactory;
import org.epos.converter.common.plugin.exception.PayloadMappingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(OrderAnnotation.class)
class InvokeAll_JavaReflectionPlugins_concurrentlyIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeAll_JavaReflectionPlugins_concurrentlyIT.class);
			
	private static PluginProxyLocator pluginProxyLocator;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		pluginProxyLocator = new PluginProxyLocator(new StandardPluginProxyFactory());
	}
	
	protected static Stream<ConversionTestData.TestConfig> createResourcePaths_VALID() throws IOException 
	{
		EnumSet<ConversionTestData> conversionsTestData = EnumSet.allOf(ConversionTestData.class);		
		final int numOfSingleInstance = 2;
		
		List<ConversionTestData.TestConfig> configs = conversionsTestData.stream()
			.flatMap(LambdaExceptionUtil.rethrowFunction(convtestdata -> {
				Set<Pair<Path, Path>> payloadPathPairs = convtestdata.getValidPayloadPaths();
				return payloadPathPairs.stream().flatMap(payloadPathPair -> 				
					IntStream.rangeClosed(1, numOfSingleInstance).mapToObj(i -> 
						 new ConversionTestData.TestConfig(
								 payloadPathPair.getLeft(),
								 payloadPathPair.getRight(),
								 convtestdata.getConversionDesc()))					
				);
			}))
			.collect(Collectors.toList());

		Collections.shuffle(configs);
		return configs.stream();
	}
	
	
	@DisplayName("Test caching of PluginProxys and PluginProxy invocations")
	@ParameterizedTest
	@MethodSource("createResourcePaths_VALID")
	@Order(1)
	void testLocateAndInvoke(ConversionTestData.TestConfig testConfig) 
	{
		try {
			// obtain input test data
			Path incomingContentPath = testConfig.incomingContentPath;
			String incomingPayload = TestUtils.readContentToString(incomingContentPath);
			ConversionDescriptor convDescriptor = testConfig.convDescriptor;
			
			// execute
			PluginProxy invoker = pluginProxyLocator.locate(convDescriptor);			
			String outgoingPayload = invoker.invoke(incomingPayload);
			
			// assert
			Path outgoingContentPath = testConfig.outgoingContentPath;
			String expectedOutgoingPayload = TestUtils.readContentToString(outgoingContentPath);
			if (expectedOutgoingPayload.charAt(0) == '[') {
				JSONAssert.assertEquals(new JSONArray(expectedOutgoingPayload), new JSONArray(outgoingPayload), false);				
			} else {
				JSONAssert.assertEquals(new JSONObject(expectedOutgoingPayload), new JSONObject(outgoingPayload), false);				
			}
			
		} catch (PluginConfigurationException | IOException | JSONException | PayloadMappingException e) {
			LOGGER.error("testLocateAndInvoke",e);
			fail();
		}
	}
	
	@DisplayName("Check contents of cache")
	@Test
	@Order(2)
	void testLocateCached() {
		
		EnumSet<ConversionTestData> conversions = EnumSet.allOf(ConversionTestData.class);
		
		// total in top-level cache
		long topLevelCount = conversions.stream()
			.map(c -> c.getConversionDesc().getExecutionDescriptor())
			.distinct()
			.count();
		
		assertEquals(topLevelCount, pluginProxyLocator.cache.size());
		
		// total in cache hierarchy
		long expected = conversions.stream()
				.map(c -> c.getConversionDesc())
				.distinct()
				.count();
		
		int actual = pluginProxyLocator.cache.keySet().stream()
				.mapToInt(execution -> pluginProxyLocator.cache.get(execution).size())
				.sum();
		
		assertEquals(expected, actual);	
	}

}
