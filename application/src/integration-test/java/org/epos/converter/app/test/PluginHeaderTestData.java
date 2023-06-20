package org.epos.converter.app.test;

import java.util.Collections;

import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.common.plugin.type.PluginProxyType;

/**
 * @author patk
 * 
 * Test plugin data class to help with navigation of the test data within the multi-module Converter project
 *
 */
public enum PluginHeaderTestData {
	
	ICSC_PLUGINS_METADATA("ics-c.plugins-metadata", "0.1.0", PluginProxyType.JAVA_REFLECTION, "plugin-icsc-plugins-metadata");
//	METADATA_KEYWORDS("PLUGIN_KEYWORDS_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-keywords"),
//	METADATA_DOMAINS("PLUGIN_DOMAINS_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-domains"),
//	TCS_QUAKEML("PLUGIN_VISUALISE_QUAKEML_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-visualise-quakeml"),
//	TCS_FDSN_STATION_XML("PLUGIN-VISUALISE-STATION_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-visualise-station"),
//	TCS_WP10_STATION("PLUGIN-VISUALISE-WP10_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-visualise-wp10"),
//	TCS_WP09_RADON("PLUGIN_VISUALISE_WP09_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-visualise-wp09"),
//	TCS_DYNVOLC("PLUGIN_VISUALISE_DYNVOLC_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-visualise-dynvolc"),
//	TCS_WP14_EPISODE("PLUGIN_VISUALISE_EPISODE_ID", "1.0.0", PluginProxyType.JAVA_REFLECTION, "plugin-visualise-episode");
	
	private PluginKey pluginKey;
	private PluginHeaderDescriptor pluginHeader;
	
	private PluginHeaderTestData(String pluginId, String pluginVersion, PluginProxyType proxyType, String pluginProjectName) 
	{
		pluginKey = new PluginKey(pluginId, pluginVersion);
		pluginHeader = new PluginHeaderDescriptor.Builder(
				pluginProjectName, 
				proxyType.getId(),	
				/* 
				 * N.b. repoArtifactLocations only needed for plugin management, not message handling
				 */
				Collections.emptyList()).build();
	}

	public PluginKey getPluginKey() {
		return pluginKey;
	}

	public PluginHeaderDescriptor getPluginHeader() {
		return pluginHeader;
	}

}
