package org.epos.converter.plugin.pluginsmetadata;

import static org.epos.converter.common.type.ContentFormat.JSON;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.common.exception.PayloadProcessingException;
import org.epos.converter.common.java.CallableJavaPlugin;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.ContentType;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PluginsMetadataCallablePluginTest {
	
	private static Logger LOG = LoggerFactory.getLogger(PluginsMetadataCallablePluginTest.class);
	
	static Stream<Arguments> createResourcePaths_forValidSimplePayloads() throws IOException
	{
		TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("icsc","pluginsmetadata"));
		
		Stream<Pair<Path, Path>> payloadPairs = resourceAccessor.getPayloadPairs(
				Paths.get("valid", "incoming_payloads"), JSON,
				Paths.get("valid", "outgoing_payloads"), JSON);
		
		return payloadPairs.map(p -> Arguments.of((Object) p));
	}

	@ParameterizedTest
	@MethodSource("createResourcePaths_forValidSimplePayloads")
	void testDoInvoke__forValidSimplePayloads(Pair<Path, Path> testResourcePair)
	{
		doValidPayloadTest(
				testResourcePair, 
				new MappingDescriptor("icsc-default",
					ContentType.APPLICATION_JSON, 
					ContentType.APPLICATION_JSON));
	}
	
	private void doValidPayloadTest(Pair<Path, Path> testResourcePair, MappingDescriptor mappingDescriptor) 
	{
		try {
			Path incomingPayloadFile = testResourcePair.getLeft();
			Path outgoingPayloadFile = testResourcePair.getRight();
			String incomingPayload = TestUtils.readContentToString(incomingPayloadFile);
			String expected = TestUtils.readContentToString(outgoingPayloadFile);
			if (LOG.isDebugEnabled()) {
				String testMsg = String.format("Testing with incoming file '%s' and outgoing file '%s'", 
						incomingPayloadFile.getFileName(),
						outgoingPayloadFile.getFileName());
				LOG.debug(testMsg);
			}
			
			CallableJavaPlugin plugin = new PluginsMetadataCallablePlugin(mappingDescriptor);
			String actual = plugin.invoke(incomingPayload);
			
			JSONAssert.assertEquals(new JSONArray(expected), new JSONArray(actual), true);
			
		} catch (PluginConfigurationException | IOException | PayloadProcessingException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
