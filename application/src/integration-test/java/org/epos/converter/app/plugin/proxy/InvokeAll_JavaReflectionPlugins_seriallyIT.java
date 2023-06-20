package org.epos.converter.app.plugin.proxy;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author patk
 * 
 * {@link PluginProxyLocator} is the CUT.
 * An of instance {@link PluginProxyLocator} is repeatedly invoked with simulated Conversion messages in a serial manner.
 * 
 */
class InvokeAll_JavaReflectionPlugins_seriallyIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeAll_JavaReflectionPlugins_seriallyIT.class);
	
	private PluginProxyLocator pluginProxyLocator;

	@BeforeEach
	void setUp() throws Exception {
		pluginProxyLocator = new PluginProxyLocator(new StandardPluginProxyFactory());
	}
	
	protected static Stream<ConversionTestData.TestConfig> createResourcePaths_VALID() throws IOException 
	{
		final EnumSet<ConversionTestData> conversionsTestData = EnumSet.allOf(ConversionTestData.class);
		final int numOfInvocations = 1;
	
		return conversionsTestData.stream().flatMap(LambdaExceptionUtil.rethrowFunction(convtestdata -> {
				Set<Pair<Path, Path>> payloadPathPairs = convtestdata.getValidPayloadPaths();
				return payloadPathPairs.stream().flatMap(payloadPathPair -> {						
					return IntStream.rangeClosed(1, numOfInvocations).mapToObj(i -> 
						 new ConversionTestData.TestConfig(								 
								 payloadPathPair.getLeft(),
								 payloadPathPair.getRight(),
								 convtestdata.getConversionDesc()));						
					});
			}));
	}
	
	@DisplayName("Test Simple PluginProxy invocations")
	@ParameterizedTest
	@MethodSource("createResourcePaths_VALID")
	@Order(1)
	void testLocateAndInvoke(ConversionTestData.TestConfig testConfig) 
	{
		try {
			// obtain input test data
			String reqContent = TestUtils.readContentToString(testConfig.incomingContentPath);
			ConversionDescriptor convDescriptor = testConfig.convDescriptor;
			
			// execute
			PluginProxy invoker = pluginProxyLocator.locate(convDescriptor);
			String outgoingPayload = invoker.invoke(reqContent);
			
			// assert
			String expectedOutgoingPayload = TestUtils.readContentToString(testConfig.outgoingContentPath);
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
		
}
