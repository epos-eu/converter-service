package org.epos.converter.app.plugin.managment;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;

public interface PluginsMetadataCache {
	
	boolean update(PluginDescriptor plugin);
	
	boolean update(Set<PluginDescriptor> plugins);
	
	Set<Pair<ConversionDescriptor, PluginKey>> findConversions(ConversionMetadata searcher);
	
	Set<PluginKey> findPlugins(boolean excludeNotInstalled, boolean excludeDisabled);
	
//	Set<PluginKey> findEnabledPlugins();
	
	PluginHeaderDescriptor findHeaderDescriptor(PluginKey key);

	void clearCache();

	Optional<ConversionSchemasDescriptor> findContentSchemasDescriptor(Pair<ConversionDescriptor, PluginKey> searcher);

	/**
	 * @param key
	 * @param isInstalled
	 * @return <code>true</code> if call led to a change in plugin's state
	 * @throws IllegalStateException if there is no {@link PluginDescriptor} associated with the {@link PluginKey} specified
	 */
	boolean setInstalled(PluginKey key, boolean isInstalled) throws IllegalStateException;
	
	/**
	 * @param key
	 * @param isEnabled
	 * @return <code>true</code> if call led to a change in plugin's state
	 * @throws IllegalStateException if there is no {@link PluginDescriptor} associated with the {@link PluginKey} specified or the plugin is not alredy installed
	 */
	boolean setEnabled(PluginKey key, boolean isEnabled) throws IllegalStateException;
	
//	Optional<ExecutionDescriptor> findExecutionDescriptor(MappingDescriptor mapping);
//	Optional<PluginHeaderDescriptor> findPluginDescriptor(String id, String version);
//	Set<PluginHeaderDescriptor> finalAllPluginDescriptors();
}
