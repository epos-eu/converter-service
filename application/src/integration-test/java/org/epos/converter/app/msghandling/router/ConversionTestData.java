package org.epos.converter.app.msghandling.router;

import static org.epos.converter.common.type.ContentFormat.JSON;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.common.test.TestResourcesAccessor;

public enum ConversionTestData {
	
	TCS_AH_EPISODES {
		@Override
		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("tcs","ah-episodes-plugin"));
			return resourceAccessor.getPayloadPairs(
							Paths.get("valid", "incoming_payloads_(wrapped)"), JSON,
							Paths.get("valid", "outgoing_payloads_(wrapped)"), JSON)
						.collect(Collectors.toSet());
		}
	},
	TCS_AH_EPISODE_ELEMENTS {
		@Override
		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("tcs","ah-episode-elements-plugin"));
			return resourceAccessor.getPayloadPairs(
							Paths.get("valid", "incoming_payloads_(wrapped)"), JSON,
							Paths.get("valid", "outgoing_payloads_(wrapped)"), JSON)
						.collect(Collectors.toSet());
		}
	},
	TCS_AH_LIST_OF_APPLICATIONS_PLUGIN {
		@Override
		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("tcs","ah-list-of-applications-plugin"));
			return resourceAccessor.getPayloadPairs(
							Paths.get("valid", "incoming_payloads_(wrapped)"), JSON,
							Paths.get("valid", "outgoing_payloads_(wrapped)"), JSON)
						.collect(Collectors.toSet());
		}
	},
	TCS_SATD_PLUGIN {
		@Override
		public Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException {
			TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Paths.get("tcs","satd-plugin"));
			return resourceAccessor.getPayloadPairs(
							Paths.get("valid", "incoming_payloads_(wrapped)"), JSON,
							Paths.get("valid", "outgoing_payloads_(wrapped)"), JSON)
						.collect(Collectors.toSet());
		}
	};
	
	public abstract Set<Pair<Path, Path>> getValidPayloadPaths() throws IOException;

}
