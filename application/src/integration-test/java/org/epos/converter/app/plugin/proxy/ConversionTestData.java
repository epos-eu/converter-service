package org.epos.converter.app.plugin.proxy;

import static org.epos.converter.common.type.ContentFormat.JSON;
import static org.epos.converter.common.type.ContentFormat.XML;
import static org.epos.converter.common.type.ContentType.APPLICATION_JSON;
import static org.epos.converter.common.type.ContentType.APPLICATION_XML;
import static org.epos.converter.common.type.ContentType.EPOS_GEO_JSON;
import static org.epos.converter.common.type.ContentType.EPOS_PLAIN_JSON;
import static org.epos.converter.common.type.ContentType.EPOS_RESULT_SET;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.test.PluginHeaderTestData;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.ContentType;

enum ConversionTestData {
	
	ICSC_PLUGINS_METADATA(PluginHeaderTestData.ICSC_PLUGINS_METADATA,
			"icsc-default", APPLICATION_JSON, APPLICATION_JSON,
			"org.epos.converter.plugin.pluginsmetadata.PluginsMetadataCallablePlugin") {
		@Override
		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("icsc","pluginsmetadata"));
			return resourceAccessor.getPayloadPairs(
							Paths.get("valid", "incoming_payloads"), JSON,
							Paths.get("valid", "outgoing_payloads"), JSON)
						.collect(Collectors.toSet());
		}		
	};
//	RESOURCE_KEYWORDS(PluginHeaderTestData.METADATA_KEYWORDS,
//			"resources/keywords", EPOS_RESULT_SET, EPOS_PLAIN_JSON,
//			"org.epos_ip.converter.plugin.keywords.KeywordsCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("keywords"));				
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads_(cleaned)", "valid"), JSON,
//							Paths.get("outgoing_payloads_(cleaned)", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	RESOURCE_DOMAINS(PluginHeaderTestData.METADATA_DOMAINS,
//			"resources/domains", EPOS_RESULT_SET, EPOS_PLAIN_JSON,
//			"org.epos_ip.converter.plugin.domains.DomainsCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("domains"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_FDSN_STATION_XML_STANDARD(PluginHeaderTestData.TCS_FDSN_STATION_XML,
//			"visualise/station/fsdn-xml", APPLICATION_XML, EPOS_GEO_JSON,
//			"org.epos.converter.plugin.visualisestation.VisualiseStationXMLCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","station","tcsxml"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), XML,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP10_STATION_STANDARD(PluginHeaderTestData.TCS_WP10_STATION,
//			"visualise/station/wp10-json", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos.converter.plugin.visualise.wp10.VisualiseWp10CallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","station","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP09_RADON_STANDARD(PluginHeaderTestData.TCS_WP09_RADON,
//			"visualise/radon/timeseries/wp09-json", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos.converter.plugin.visualise.wp09.VisualiseWp09RadonTimeSeriesCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","radon","tcsjson"));
//			
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_DYNVOLC_STANDARD(PluginHeaderTestData.TCS_DYNVOLC,
//			"visualise/dynvolc/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualisedynvolc.VisualiseDynvolcCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","dynvolc","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP14_EPISODE_SIMPLE(PluginHeaderTestData.TCS_WP14_EPISODE,
//			"visualise/episode/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualiseepisode.VisualiseEpisodeCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP14_EPISODE_ELEMENTS(PluginHeaderTestData.TCS_WP14_EPISODE,
//			"visualise/episode-elements/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualiseepisode.VisualiseEpisodeElementsCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode-elements","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	},
//	TCS_WP14_EPISODE_ELEMENTS_LIST(PluginHeaderTestData.TCS_WP14_EPISODE,
//			"visualise/episode-elements-list/tcsjson", APPLICATION_JSON, EPOS_GEO_JSON,
//			"org.epos_ip.converter.plugin.visualiseepisode.VisualiseEpisodeElementsCallablePlugin") {
//		@Override
//		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
//			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("visualise","episode-elements-list","tcsjson"));
//			return resourceAccessor.getPayloadPairs(
//							Paths.get("incoming_payloads", "valid"), JSON,
//							Paths.get("outgoing_payloads", "valid"), JSON)
//						.collect(Collectors.toSet());
//		}
//	};
	
	abstract Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException;
	
	private PluginHeaderTestData pluginDetails;
	private ConversionDescriptor conversionDesc;
	
	ConversionTestData(PluginHeaderTestData pluginDetails, 
			String reqType, ContentType reqContentType, ContentType respContentType, 
			String invokerClass) 
	{
		this.pluginDetails = pluginDetails;
		
		Path pluginInstallRelPath = Path.of(pluginDetails.getPluginHeader().getInstallLocation());
		Path targetArtifactRelPath = Paths.get("target");
		Path pluginInstallAbsPath = Path.of(System.getProperty("user.dir"), "..")
				.resolve(pluginInstallRelPath)
				.normalize();
		
		Path targetArtifactAbsPath = pluginInstallAbsPath.resolve(targetArtifactRelPath);
		String targetPluginJarName = TestUtils.findNameOfTargetJar(targetArtifactAbsPath.toString());

		// ConversionDescriptor (common model)
		var executionDescCommon = new org.epos.converter.common.plugin.type.ExecutionDescriptor(
				pluginDetails.getPluginKey().getId(),
				pluginDetails.getPluginHeader().getProxyType(),
				pluginInstallAbsPath.toString(),
				targetArtifactRelPath.toString(),
				targetPluginJarName,
				invokerClass);
		
		var mappingDescCommon = new MappingDescriptor(reqType, reqContentType, respContentType);
		
		conversionDesc = new ConversionDescriptor(executionDescCommon, mappingDescCommon);
	}
	
	public PluginHeaderTestData getPluginDetails() {
		return pluginDetails;
	}

	public ConversionDescriptor getConversionDesc() {
		return conversionDesc;
	}
	
	 /*
	 * Supports test harness for integration testing within the multi-module project
	 */
	static class TestConfig {		
		Path incomingContentPath; 
		Path outgoingContentPath;
		ConversionDescriptor convDescriptor;

		TestConfig(Path incomingContentPath, Path outgoingContentPath, ConversionDescriptor convDescriptor) 
		{
			this.incomingContentPath = incomingContentPath;
			this.outgoingContentPath = outgoingContentPath;
			this.convDescriptor = convDescriptor;
		}
	}

}
