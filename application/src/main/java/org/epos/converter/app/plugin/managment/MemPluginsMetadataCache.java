package org.epos.converter.app.plugin.managment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.app.plugin.managment.model.PluginStatus;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;
import org.epos.converter.common.type.ContentType;
import org.springframework.stereotype.Repository;

@Repository
public class MemPluginsMetadataCache implements PluginsMetadataCache {
	
	private Map<PluginKey, PluginHeaderDescriptor> pluginHeaders = new HashMap<>();
	private Set<Pair<ConversionDescriptor, PluginKey>> conversions = new HashSet<>();
	private Map<Pair<ConversionDescriptor, PluginKey>, ConversionSchemasDescriptor> contentSchemas = new HashMap<>();
	
	/* Unlike the entities of the PluginDescriptor PluginStatus instance are mutable:
	 * They represent the state of a plugin installed on a Converter service instance.
	 */
	private Map<PluginKey, PluginStatus> pluginStatuses = new HashMap<>();
	
	@Override
	public void clearCache() {
		pluginStatuses.clear();
		pluginHeaders.clear();
		conversions.clear();
	}
	
	@Override
	public boolean update(PluginDescriptor plugin)
	{
		Objects.requireNonNull(plugin);		
		synchronized(this) {
			return delegatedUpdate(plugin);			
		}
	}
	
	@Override
	public boolean update(Set<PluginDescriptor> plugins) 
	{
		Objects.requireNonNull(plugins);
		synchronized(this) {
			boolean modified = false;
			
			for (PluginDescriptor plugin : plugins) {				
				modified |= delegatedUpdate(plugin);			
			}
			return modified;
		}
	}
	
	/**
	 * @param plugin
	 * @return <code>true</code> if update led to a change in the plugin metadata. 
	 */
	private boolean delegatedUpdate(PluginDescriptor plugin) 
	{
		PluginKey pluginKey = plugin.getKey();
		PluginHeaderDescriptor pluginDescriptor = plugin.getHeader();
		boolean modified = false;
		
		// update plugin header			
		PluginHeaderDescriptor prevHeader = pluginHeaders.put(pluginKey, pluginDescriptor);
		modified |= (prevHeader != null);
		
		// add plugin status if new plugin
		pluginStatuses.computeIfAbsent(pluginKey, k -> new PluginStatus());		
		
		// update conversions
		Set<Pair<ConversionDescriptor, PluginKey>> pluginConversions = plugin.getConversions().stream()
			.map(conversion -> new ImmutablePair<>(conversion, pluginKey))
			.collect(Collectors.toSet());
		
		modified |= conversions.addAll(pluginConversions);
	
		return modified;
	}


	@Override
	public Set<Pair<ConversionDescriptor, PluginKey>> findConversions(ConversionMetadata searcher) 
	{
		Predicate<Pair<ConversionDescriptor, PluginKey>> filters = e -> {
			PluginStatus pluginStatus = pluginStatuses.get(e.getRight());
			return (pluginStatus != null) && pluginStatus.isEnabled();
		};
		
		// Mandatory: mapping request type
		filters = filters.and(e -> 
			searcher.getConversionRequestType().equals(e.getLeft().getMapping().getRequestType()));
		
		// Optional: mapping request content type
		if (searcher.getConversionRequestContentType().isPresent()) {
			filters = filters.and(e -> { 
				ContentType conversionRequestContentType = searcher.getConversionRequestContentType().get();
				return conversionRequestContentType.equals(e.getLeft().getMapping().getRequestContentType());
			});
		}
		
		// Optional: mapping response content type
		if (searcher.getConversionResponseContentType().isPresent()) {
			filters = filters.and(e -> { 
				ContentType conversionResponseContentType = searcher.getConversionResponseContentType().get();
				return conversionResponseContentType.equals(e.getLeft().getMapping().getResponseContentType());
			});
		}
		
		// Optional: plugin id
		if (searcher.getPluginId().isPresent()) {
			filters = filters.and(e -> { 
				String pluginId = searcher.getPluginId().get();
				return pluginId.equals(e.getRight().getId());
			});
		}
		
		// Optional: plugin version
		if (searcher.getPluginVersion().isPresent()) {
			filters = filters.and(e -> { 
				String pluginVersion = searcher.getPluginVersion().get();
				return pluginVersion.equals(e.getRight().getVersion());
			});
		}
		
		// ensure defensive copy returned
		return conversions.stream()
			.filter(filters)
			.map(conv -> {
				ConversionDescriptor convDesc = ConversionDescriptor.newInstance(conv.getLeft());
				PluginKey pluginKey = conv.getRight();
				return new ImmutablePair<>(ConversionDescriptor.newInstance(convDesc), pluginKey);
			})
			.collect(Collectors.toSet());
	}

	@Override
	public PluginHeaderDescriptor findHeaderDescriptor(PluginKey key) {
		return pluginHeaders.get(key);
	}
	
	@Override
	public Set<PluginKey> findPlugins(boolean excludeNotInstalled, boolean excludeDisabled) 
	{
		Predicate<Entry<PluginKey, PluginStatus>> filters = e -> true; 
		
		if (excludeNotInstalled) {
			filters.and(e -> e.getValue().isInstalled());
		}
		
		if (excludeDisabled) {
			filters.and(e -> e.getValue().isEnabled());
		}

		return pluginStatuses.entrySet().stream()
				.filter(filters)
				.map(e -> e.getKey())
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public Optional<ConversionSchemasDescriptor> findContentSchemasDescriptor(
			Pair<ConversionDescriptor, PluginKey> searcher) 
	{
		return Optional.ofNullable(contentSchemas.get(searcher));
	}

	@Override
	public boolean setInstalled(PluginKey key, boolean isInstalled) throws IllegalStateException
	{
		Objects.requireNonNull(key);
		
		synchronized(this) {
			if (!pluginHeaders.containsKey(key)) {
				throw new IllegalStateException("Unexpected Plugin state for %s plugin: No header descriptor found.");
			}
			PluginStatus pluginStatus = pluginStatuses.get(key);
			if (pluginStatus.isInstalled() != isInstalled) {
				pluginStatus.setInstalled(isInstalled);
			}
			
			return isInstalled == pluginStatus.isInstalled();
		}
	}

	@Override
	public boolean setEnabled(PluginKey key, boolean isEnabled) throws IllegalStateException
	{
		Objects.requireNonNull(key);
		
		synchronized(this) {
			PluginStatus pluginStatus = pluginStatuses.get(key);
			if (pluginStatus == null || (!pluginStatus.isInstalled() && isEnabled)) {
				throw new IllegalStateException("Plugin state indicates it has not be installed: Cannot set plugin state to "
						+ "enabled without it being installed");
			}
			pluginStatus.setEnabled(isEnabled);
			return isEnabled == pluginStatus.isEnabled();
		}
	}

}
