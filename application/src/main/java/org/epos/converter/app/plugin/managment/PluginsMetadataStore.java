package org.epos.converter.app.plugin.managment;

import java.util.Optional;

import org.epos.converter.app.plugin.managment.exception.PluginStoreAccessException;

public interface PluginsMetadataStore {
	
	Optional<String> getAllPluginsMetadata() throws PluginStoreAccessException;

}
