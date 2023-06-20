package org.epos.converter.app.msghandling.conversion;

import static org.epos.converter.app.msghandling.conversion.ConversionTestData.ICSC_PLUGINS_METADATA;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.configuration.properties.PluginsConfigProperties;
import org.epos.converter.app.msghandling.conversion.ConversionTestData.TestConfig;
import org.epos.converter.app.msghandling.exception.HandlingConfigurationException;
import org.epos.converter.app.msghandling.exception.MessageProcessingException;
import org.epos.converter.app.plugin.managment.ConversionMetadata;
import org.epos.converter.app.plugin.managment.PluginsMetadataCache;
import org.epos.converter.app.plugin.managment.PluginsMetadataCacheAdapter;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.app.plugin.proxy.PluginProxyLocator;
import org.epos.converter.app.plugin.proxy.impl.StandardPluginProxyFactory;
import org.epos.converter.app.test.PluginHeaderTestData;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
class ConversionHandlerAll_JavaReflectionPluginsIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConversionHandlerAll_JavaReflectionPluginsIT.class);
	
	private static ConversionMessageHandler handler;
	private static Path pluginInstallAbsPath = Path.of(System.getProperty("user.dir"), "..");
	private static PluginProxyLocator pluginProxyLocator = new PluginProxyLocator(
			new StandardPluginProxyFactory());
	private static EnumMap<ConversionTestData, ConversionMetadata> conversionsUT;
	
	@Autowired
    private PluginsMetadataCache metadataCache;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception 
	{
		// Create plugin store queries designed to correspond to specific conversions
		conversionsUT = new EnumMap<>(ConversionTestData.class);
		conversionsUT.put(ICSC_PLUGINS_METADATA,
				new ConversionMetadata.Builder("icsc-default")
					.build());
//		conversionsUT.put(TCS_DYNVOLC_STANDARD, 
//				new ConversionMetadata.Builder("visualise/dynvolc/tcsjson")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_FDSN_STATION_XML_STANDARD,
//				new ConversionMetadata.Builder("visualise/station/fsdn-xml")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_QUAKEML_STANDARD,
//				new ConversionMetadata.Builder("visualise/quakeml/tcsxml")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_WP09_RADON_STANDARD, 
//				new ConversionMetadata.Builder("visualise/radon/timeseries/wp09-json")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_WP10_STATION_STANDARD,
//				new ConversionMetadata.Builder("visualise/station/wp10-json")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_WP14_EPISODE_ELEMENTS,
//				new ConversionMetadata.Builder("visualise/episode-elements/tcsjson")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_WP14_EPISODE_ELEMENTS_LIST, 
//				new ConversionMetadata.Builder("visualise/episode-elements-list/tcsjson")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
//		conversionsUT.put(TCS_WP14_EPISODE_SIMPLE,
//				new ConversionMetadata.Builder("visualise/episode/tcsjson")
//					.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
//					.build());
	}
	
	@BeforeEach
	void setUp() throws Exception 
	{
		// Populate test plugin store
		Map<PluginHeaderTestData, List<ConversionDescriptor>> pluginConversions = Arrays.stream(ConversionTestData.values())
				.collect(Collectors.groupingBy(
						ConversionTestData::getPluginDetails,
						Collectors.mapping(ConversionTestData::getConversionDesc, Collectors.toList())));
				
		Set<PluginDescriptor> pluginDescriptors = pluginConversions.entrySet().stream().map(e -> {
					PluginKey pluginKey = e.getKey().getPluginKey();
					PluginHeaderDescriptor pluginHeader = e.getKey().getPluginHeader();
					return new PluginDescriptor(pluginKey, pluginHeader, e.getValue());
				})
				.collect(Collectors.toSet());
	
		metadataCache.update(pluginDescriptors);
		
		// update all plugins to be enabled for the purpose of these tests!
		Set<PluginKey> allPlugins = metadataCache.findPlugins(false, false);
		allPlugins.forEach(p -> {
			metadataCache.setInstalled(p, true);
			metadataCache.setEnabled(p, true);
		});
	}

	/**
	 * @return {@link TestConfig}s representing valid conversions
	 * @throws IOException
	 */
	protected static Stream<ConversionTestData.TestConfig> createResourcePaths_VALID() throws IOException 
	{
		return conversionsUT.keySet().stream()
			.flatMap(LambdaExceptionUtil.rethrowFunction(conversion -> {
				Set<Pair<Path, Path>> payloadPathPairs = conversion.getValidPayloadPaths();
				return payloadPathPairs.stream().map(payloadPathPair -> 
														new ConversionTestData.TestConfig(
																payloadPathPair.getLeft(),
																payloadPathPair.getRight(),
																conversionsUT.get(conversion),
																conversion.getPluginMode()));
			}));
	}
	
	@DisplayName("Test ConversionMessageHandler implementation when content valid")
	@ParameterizedTest
	@MethodSource("createResourcePaths_VALID")
	void testLocateAndInvoke(ConversionTestData.TestConfig testConfig) 
	{		
		// Create a MessageHandler for ConversionMessages		
		PluginsConfigProperties pluginsConfigurationProps = new PluginsConfigProperties(pluginInstallAbsPath, testConfig.pluginMode.toString(), null);
		PluginsMetadataCacheAdapter pluginMetadataAccessor = new PluginsMetadataCacheAdapter(metadataCache, pluginsConfigurationProps);
		handler = new ConversionMessageHandler(pluginMetadataAccessor, pluginProxyLocator);
		
		try {
			String incomingContent = TestUtils.readContentToString(testConfig.incomingContentPath);
			ConversionMetadata convMetadata = testConfig.pluginStoreQuery;
			
			ConversionMessage convMessage = new ConversionMessage(incomingContent, convMetadata);	
			ConversionMessage response = handler.handle(convMessage);
			String actualConvertedContent = response.getContent();
			
			String expectedConvertedContent = TestUtils.readContentToString(testConfig.outgoingContentPath);
			
			if (expectedConvertedContent.charAt(0) == '[') {
				JSONAssert.assertEquals(new JSONArray(expectedConvertedContent), new JSONArray(actualConvertedContent), false);
			}else{
				JSONAssert.assertEquals(new JSONObject(expectedConvertedContent), new JSONObject(actualConvertedContent), false);
			}
			
		} catch (IOException | MessageProcessingException | JSONException | HandlingConfigurationException e) {
			LOGGER.error("testLocateAndInvoke", e);
			fail();
		}

	}

	/**
	 * @return {@link TestConfig}s representing incoming content that should fail schema validation
	 * @throws IOException
	 */
	protected static Stream<ConversionTestData.TestConfig> createResourcePaths_INVALID_Schema() throws IOException 
	{
		return conversionsUT.keySet().stream()
			.flatMap(LambdaExceptionUtil.rethrowFunction(conversion -> {
				Set<Path> contentPaths = conversion.getInvalidSchemaPayloadPaths();
				return contentPaths.stream().map(contentPath -> 
							 new ConversionTestData.TestConfig(contentPath, null,
									 conversionsUT.get(conversion), 
									 conversion.getPluginMode()));
			}));
	}
	
}
