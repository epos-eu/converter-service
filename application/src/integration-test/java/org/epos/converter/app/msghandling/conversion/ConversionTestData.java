package org.epos.converter.app.msghandling.conversion;

import static org.epos.converter.common.type.ContentFormat.JSON;
import static org.epos.converter.common.type.ContentType.APPLICATION_JSON;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.plugin.managment.ConversionMetadata;
import org.epos.converter.app.plugin.managment.PluginsMode;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.ExecutionDescriptor;
import org.epos.converter.app.plugin.managment.model.MappingDescriptor;
import org.epos.converter.app.test.PluginHeaderTestData;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.ContentType;

/**
 * @author patk
 *
 * Represents Conversion details at the conversion message handler-level for internal test execution along with access details to
 * associated test data.
 * 
 * This implementation assumes a standard Maven project structure for the plugins under test.
 */
enum ConversionTestData {
	
	ICSC_PLUGINS_METADATA(PluginsMode.ICSC_V1, PluginHeaderTestData.ICSC_PLUGINS_METADATA,
			"icsc-default",
			APPLICATION_JSON, APPLICATION_JSON,
			"org.epos.converter.plugin.pluginsmetadata.PluginsMetadataCallablePlugin")
	{
		@Override
		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("icsc","pluginsmetadata"));
			return resourceAccessor.getPayloadPairs(
							Paths.get("valid", "incoming_payloads"), JSON,
							Paths.get("valid", "outgoing_payloads"), JSON)
						.collect(Collectors.toSet());
		}

		@Override
		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
			return Collections.emptySet();
		}		
	};	
//	RESOURCE_KEYWORDS(PluginsMode.FLEXIBILE, PluginHeaderTestData.METADATA_KEYWORDS,
//			"resources/keywords", EPOS_RESULT_SET, EPOS_PLAIN_JSON,
//			"org.epos_ip.converter.plugin.keywords.KeywordsCallablePlugin") 
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("keywords"));				
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads_(cleaned)", "valid"), JSON,
//							Paths.get("outgoing_payloads_(cleaned)", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			return Collections.emptySet();
//		}
//	},
//	RESOURCE_DOMAINS(PluginsMode.FLEXIBILE, PluginHeaderTestData.METADATA_DOMAINS,
//			"resources/domains", EPOS_RESULT_SET, EPOS_PLAIN_JSON,
//			"org.epos_ip.converter.plugin.domains.DomainsCallablePlugin")
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("domains"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			return Collections.emptySet();
//		}
//	},
//	TCS_QUAKEML_STANDARD(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_QUAKEML,
//			"visualise/quakeml/tcsxml", APPLICATION_XML, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualisequakeml.VisualiseQuakemlCallablePlugin",
//			Paths.get("schemas","payload_incoming","quakeml.xsd"),
//			Paths.get("schemas","payload_outgoing","geojson_(quakeml).json"))
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","quakeml","tcsxml"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), XML,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","quakeml","tcsxml"));
//			return resourceAccessor.getPayloadIncoming(
//							Paths.get("incoming_payloads", "invalid"), JSON)
//						.collect(Collectors.toSet());
//		}		
//	},
//	TCS_FDSN_STATION_XML_STANDARD(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_FDSN_STATION_XML,
//			"visualise/station/fsdn-xml", APPLICATION_XML, EPOS_GEO_JSON,
//			"org.epos.converter.plugin.visualisestation.VisualiseStationXMLCallablePlugin",
//			Paths.get("schemas","payload_incoming","fdsn-station-1.0.xsd"),
//			Paths.get("schemas","payload_outgoing","epos-geojson-1.0.json")) {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","station","tcsxml"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), XML,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			return Collections.emptySet();
//		}
//	},
//	TCS_WP10_STATION_STANDARD(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_WP10_STATION,
//			"visualise/station/wp10-json", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos.converter.plugin.visualise.wp10.VisualiseWp10CallablePlugin",
//			Paths.get("schemas","in","wp10-station-1.0.json"),
//			Paths.get("schemas","out","epos-geojson-1.0.json")) 
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","station","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			return Collections.emptySet();
//		}
//	},
//	TCS_WP09_RADON_STANDARD(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_WP09_RADON,
//			"visualise/radon/timeseries/wp09-json", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos.converter.plugin.visualise.wp09.VisualiseWp09RadonTimeSeriesCallablePlugin",
//			Paths.get("schemas","radon","time-series","in","wp09-radon-timeseries-1.0.json"),
//			Paths.get("schemas","radon","time-series","out","epos-geojson-1.0.json")) 
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","radon","tcsjson"));
//			
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","radon","tcsjson"));
//			return resourceAccessor.getPayloadIncoming(
//							Paths.get("incoming_payloads", "schema_failures"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_DYNVOLC_STANDARD(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_DYNVOLC,
//			"visualise/dynvolc/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualisedynvolc.VisualiseDynvolcCallablePlugin",
//			Paths.get("schemas","payload_incoming","wp11-dynvolc.json"),
//			Paths.get("schemas","payload_outgoing","geojson_(dynvolc).json"))
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","dynvolc","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			return Collections.emptySet();
//		}
//	},
//	TCS_WP14_EPISODE_SIMPLE(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_WP14_EPISODE,
//			"visualise/episode/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualiseepisode.VisualiseEpisodeCallablePlugin",
//			Paths.get("schemas","payload_incoming","simple","episode.json"),
//			Paths.get("schemas","payload_outgoing","simple","episode_geojson.json"))
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode","tcsjson"));
//			return resourceAccessor.getPayloadIncoming(
//							Paths.get("incoming_payloads", "schema_failures"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP14_EPISODE_ELEMENTS(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_WP14_EPISODE,
//			"visualise/episode-elements/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualiseepisode.VisualiseEpisodeElementsCallablePlugin",
//			Paths.get("schemas","payload_incoming","elements","episode-elements.json"),
//			Paths.get("schemas","payload_outgoing","elements","episode-elements_geojson.json"))
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode-elements","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode-elements","tcsjson"));
//			return resourceAccessor.getPayloadIncoming(
//							Paths.get("incoming_payloads", "schema_failures"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP14_EPISODE_ELEMENTS_LIST(PluginsMode.FLEXIBILE, PluginHeaderTestData.TCS_WP14_EPISODE,
//			"visualise/episode-elements-list/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualiseepisode.VisualiseEpisodeElementsCallablePlugin",
//			Paths.get("schemas","payload_incoming","elements","episode-elements-list.json"),
//			Paths.get("schemas","payload_outgoing","elements","episode-elements-list_geojson.json"))
//	{
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode-elements-list","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//
//		@Override
//		public Set<Path> getInvalidSchemaPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode-elements-list","tcsjson"));
//			return resourceAccessor.getPayloadIncoming(
//							Paths.get("incoming_payloads", "schema_failures"), JSON)
//						.collect(Collectors.toSet());
//		}
//	};
	
	public abstract Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException;
	public abstract Set<Path> getInvalidSchemaPayloadPaths() throws IOException;
	
	private PluginsMode pluginMode;
	private PluginHeaderTestData pluginDetails;
	private ConversionDescriptor conversionDesc;
	private org.epos.converter.common.plugin.type.ConversionDescriptor conversionDescCommon;
	private Optional<Path> requestSchemaLocation = Optional.empty();
	private Optional<Path> responseSchemaLocation = Optional.empty();

	ConversionTestData(PluginsMode pluginMode, PluginHeaderTestData pluginDetails, 
			String reqType, ContentType reqContentType, ContentType respContentType, String invokerClass) 
	{
		this(pluginMode, pluginDetails, reqType, reqContentType, respContentType, invokerClass, null, null);
	}
	
	ConversionTestData(PluginsMode pluginMode, PluginHeaderTestData pluginDetails, 
			String reqType, ContentType reqContentType, ContentType respContentType, String invokerClass,
			Path reqSchemaResourceRelPath, Path respSchemaResourceRelPath) 
	{		
		this.pluginMode = pluginMode;
		this.pluginDetails = pluginDetails;
		
		Path pluginInstallRelPath = Path.of(pluginDetails.getPluginHeader().getInstallLocation());
		Path targetArtifactRelPath = Paths.get("target");
		Path pluginInstallAbsPath = Path.of(System.getProperty("user.dir"), "..")
				.resolve(pluginInstallRelPath)
				.normalize();
		
		Path targetArtifactAbsPath = pluginInstallAbsPath.resolve(targetArtifactRelPath);
		String targetPluginJarName = TestUtils.findNameOfTargetJar(targetArtifactAbsPath.toString());
		
		// ConversionDescriptor (plugin management model)
		String[] invocationDetail = { targetArtifactRelPath.toString(), targetPluginJarName, invokerClass };
		ExecutionDescriptor executionDesc = new ExecutionDescriptor(invocationDetail);
		
		MappingDescriptor mappingDesc = new MappingDescriptor.Builder(reqType)
				.withRequestContentType(reqContentType.getValue())
				.withResponseContentType(respContentType.getValue())
				.build();
		
		conversionDesc = new ConversionDescriptor.Builder(mappingDesc, executionDesc).build();
		
		// ConversionDescriptor (common model)
		var executionDescCommon = new org.epos.converter.common.plugin.type.ExecutionDescriptor(
				pluginDetails.getPluginKey().getId(),
				pluginDetails.getPluginHeader().getProxyType(),
				pluginInstallAbsPath.toString(),
				targetArtifactRelPath.toString(),
				targetPluginJarName,
				invokerClass);
		
		var mappingDescCommon = new org.epos.converter.common.plugin.type.MappingDescriptor(
				reqType, reqContentType, respContentType);
		
		conversionDescCommon = new org.epos.converter.common.plugin.type.ConversionDescriptor(executionDescCommon, mappingDescCommon);
		
		// Content schemas
		final Path pluginResourcesRelativePath = pluginInstallAbsPath.resolve(Paths.get("src","main","resources")).normalize();
		
		if (reqSchemaResourceRelPath != null) {
			requestSchemaLocation = Optional.of(pluginResourcesRelativePath.resolve(reqSchemaResourceRelPath));
		}
		if (respSchemaResourceRelPath != null) {
			responseSchemaLocation = Optional.of(pluginResourcesRelativePath.resolve(respSchemaResourceRelPath));
		}

	}

	public ConversionDescriptor getConversionDesc() {
		return conversionDesc;
	}
	public Optional<Path> getRequestSchemaLocation() {
		return requestSchemaLocation;
	}
	public Optional<Path> getResponseSchemaLocation() {
		return responseSchemaLocation;
	}
	public PluginHeaderTestData getPluginDetails() {
		return pluginDetails;
	}
	public org.epos.converter.common.plugin.type.ConversionDescriptor getConversionDescCommon() {
		return conversionDescCommon;
	}
	public PluginsMode getPluginMode() {
		return pluginMode;
	}
	
	 /*
	 * Supports test harness for integration testing within the multi-module project
	 */
	static class TestConfig {		
		Path incomingContentPath; 
		Path outgoingContentPath;
		ConversionMetadata pluginStoreQuery;
		PluginsMode pluginMode;

		TestConfig(Path incomingContentPath, Path outgoingContentPath, 
				ConversionMetadata pluginStoreQuery, PluginsMode pluginMode) 
		{
			this.incomingContentPath = incomingContentPath;
			this.outgoingContentPath = outgoingContentPath;
			this.pluginStoreQuery = pluginStoreQuery;
			this.pluginMode = pluginMode;
		}
	}

}
