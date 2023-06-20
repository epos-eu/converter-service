package org.epos.converter.app.msghandling.router;

import static org.epos.router_framework.domain.BuiltInActorType.WEB_API;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.msghandling.conversion.ConversionMessageHandler;
import org.epos.converter.app.plugin.managment.PluginsMetadataCache;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.exception.RoutingMessageHandlingException;
import org.epos.router_framework.types.ServiceType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
class DefaultRoutingHandlerIT {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRoutingHandlerIT.class);
	
	@Autowired
	ConversionMessageHandler conversionHandler;
	
	@Autowired
    PluginsMetadataCache metadataCache;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception 
	{
		// update all plugins to be enabled for the purpose of these tests!
		Set<PluginKey> allPlugins = metadataCache.findPlugins(false, false);
		allPlugins.forEach(p -> {
			metadataCache.setInstalled(p, true);
			metadataCache.setEnabled(p, true);
		});
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	/**
	 * @return {@link TestConfig}s representing valid conversions
	 * @throws IOException
	 */
	protected static Stream<Pair<Path, Path>> createResourcePaths_VALID() throws IOException 
	{
		return Stream.of(ConversionTestData.values())
			.flatMap(LambdaExceptionUtil.rethrowFunction(conv -> conv.getValidPayloadPaths().stream()));
	}

	@DisplayName("Test wrapped incoming payloads")
	@ParameterizedTest
	@MethodSource("createResourcePaths_VALID")
	void testHandleStringServiceTypeMapOfStringObject(Pair<Path, Path> testDataFiles) throws RoutingMessageHandlingException
	{
		try {
			Path incomingPayloadFile = testDataFiles.getLeft();
			Path outgoingPayloadFile = testDataFiles.getRight();
			
			if (LOG.isDebugEnabled()) {
				String testMsg = String.format("Testing with incoming file '%s' and outgoing file '%s'", 
						incomingPayloadFile.toString(),
						outgoingPayloadFile.toString());
				LOG.debug(testMsg);
			}
			
			// obtain input test data
			String reqPayload = TestUtils.readContentToString(incomingPayloadFile);
			String expectedRespPayload = TestUtils.readContentToString(outgoingPayloadFile);

			// execute
			DefaultRoutingHandler routingHandler = new DefaultRoutingHandler(
					Actor.getInstance(WEB_API), 
					conversionHandler);
			String respPayload = routingHandler.handle(reqPayload, ServiceType.EXTERNAL, Collections.emptyMap())
											.toString();
			
			if (LOG.isDebugEnabled()) {
				String logMsgAcual = String.format("Actual conversion response to '%s'...%n%s", 
						incomingPayloadFile.getFileName(),
						respPayload);
				LOG.debug(logMsgAcual);
				
				String logMsgExpected = String.format("Expected conversion response to '%s'...%n%s", 
						incomingPayloadFile.getFileName(),
						expectedRespPayload);
				LOG.debug(logMsgExpected);				
			}
			
			// assert			
			if (expectedRespPayload.charAt(0) == '[') {
				JSONAssert.assertEquals(new JSONArray(expectedRespPayload), new JSONArray(respPayload), false);
			} else {
				JSONAssert.assertEquals(new JSONObject(expectedRespPayload), new JSONObject(respPayload), false);				
			}
		} catch (IOException e) {
			LOG.error("handle", e);
			fail();
		}

	}

}
