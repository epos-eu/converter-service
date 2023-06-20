package org.epos.converter.app.plugin.managment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemPluginsMetadataCacheTest {
	
	private static Set<PluginDescriptor> pluginsMetadata;
	
	private PluginsMetadataCache pluginStore;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("payloads", "plugin-metadata"), true);
		// TODO: write 'plugin-metadata-schema.json'
		Path jsonSchemaPath = Paths.get("schemas", "plugin-metadata-schema.json");
	
		Path resourcePath = resourceAccessor.getResourcePath(Paths.get("valid", "plugin-metatdata_internal_resource_(converted)_#03.json"));
		String content = TestUtils.readContentToString(resourcePath);

		PluginDescriptorsReader reader = new PluginDescriptorsReaderJson(jsonSchemaPath.toString());
		pluginsMetadata = reader.parse(content);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		pluginStore = new MemPluginsMetadataCache();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testUpdate() {
		boolean updated = pluginStore.update(pluginsMetadata);
		assertTrue(updated);
	}
	
	@Test
	void testFindConversions_PluginsNotInstalled() throws IOException 
	{
		pluginStore.update(pluginsMetadata);
		
		ConversionMetadata searcher = null;
		Set<Pair<ConversionDescriptor, PluginKey>> conversions = null;
		
		searcher = new ConversionMetadata.Builder("visualise/episode/tcs").build();
		conversions = pluginStore.findConversions(searcher);
		
		// Plugins present with available Conversions but plugins not marked as "installed"
		assertEquals(0, conversions.size());
	}
	
	@Test
	void testFindConversions_PluginsNotActivated() throws IOException 
	{
		pluginStore.update(pluginsMetadata);
		markAllPluginsAsInstalled();
		
		ConversionMetadata searcher = null;
		Set<Pair<ConversionDescriptor, PluginKey>> conversions = null;
		
		searcher = new ConversionMetadata.Builder("visualise/episode/tcs").build();
		conversions = pluginStore.findConversions(searcher);
		
		// Plugins present with available Conversions but plugins not marked as "installed"
		assertEquals(0, conversions.size());
	}

	@Test
	void testFindConversions_ByRequestTypeOnly() throws IOException 
	{
		pluginStore.update(pluginsMetadata);
		markAllPluginsAsInstalled();
		markAllPluginsAsEnabled();
		
		ConversionMetadata searcher = null;
		Set<Pair<ConversionDescriptor, PluginKey>> conversions = null;
		
		searcher = new ConversionMetadata.Builder("visualise/episode/tcs").build();
		conversions = pluginStore.findConversions(searcher);
		assertEquals(2, conversions.size());
		
		searcher = new ConversionMetadata.Builder("visualise/episode-elements/tcs").build();
		conversions = pluginStore.findConversions(searcher);
		assertEquals(3, conversions.size());
		
		searcher = new ConversionMetadata.Builder("https://catalog.terradue.com/gep-epos/SatelliteObservations/WebService/WRAPPED_INTERFEROGRAM/Operation/Search").build();
		conversions = pluginStore.findConversions(searcher);
		assertEquals(1, conversions.size());
		
		searcher = new ConversionMetadata.Builder("visualise/episode-elements/fishcakes").build();
		conversions = pluginStore.findConversions(searcher);
		assertEquals(0, conversions.size());
	}

	@Test
	void testFindConversions_ByAllMappingDetail() throws IOException 
	{
		pluginStore.update(pluginsMetadata);
		markAllPluginsAsInstalled();
		markAllPluginsAsEnabled();
		
		ConversionMetadata searcher = null;
		Set<Pair<ConversionDescriptor, PluginKey>> conversions = null;
		
		searcher = new ConversionMetadata.Builder("visualise/episode-elements/tcs")
				.withConversionRequestContentType(ContentType.APPLICATION_JSON)
				.withConversionResponseContentType(ContentType.EPOS_GEO_JSON)
				.build();
		conversions = pluginStore.findConversions(searcher);
		assertEquals(1, conversions.size());
		
		searcher = new ConversionMetadata.Builder("visualise/episode-elements/tcs")
				.withConversionRequestContentType(ContentType.APPLICATION_JSON)
				.withConversionResponseContentType(ContentType.APPLICATION_JSON)
				.build();
		conversions = pluginStore.findConversions(searcher);
		assertEquals(0, conversions.size());		
	}
	
	private void markAllPluginsAsInstalled() {
		Set<PluginKey> allPlugins = pluginStore.findPlugins(false, false);
		allPlugins.forEach(e -> {
			pluginStore.setInstalled(e, true);			
		});
	}
	
	private void markAllPluginsAsEnabled() {
		Set<PluginKey> allPlugins = pluginStore.findPlugins(false, false);
		allPlugins.forEach(e -> {
			pluginStore.setEnabled(e, true);			
		});
	}


}
