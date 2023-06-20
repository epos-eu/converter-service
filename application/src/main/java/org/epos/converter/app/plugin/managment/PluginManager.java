package org.epos.converter.app.plugin.managment;

import static java.util.Collections.singleton;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.app.configuration.properties.PluginCacheSyncTimings;
import org.epos.converter.app.msghandling.conversion.ConversionMessage;
import org.epos.converter.app.msghandling.conversion.ConversionMessageHandler;
import org.epos.converter.app.msghandling.exception.HandlingConfigurationException;
import org.epos.converter.app.msghandling.exception.MessageProcessingException;
import org.epos.converter.app.plugin.managment.exception.PluginInstallationException;
import org.epos.converter.app.plugin.managment.exception.PluginStoreAccessException;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


/**
 * Manages the syncing of the external Plugins Metadata store with the internal Plugs Metadata cache.
 */
@Service
@EnableConfigurationProperties(PluginCacheSyncTimings.class)
public class PluginManager {

	private static final Logger LOG = LoggerFactory.getLogger(PluginManager.class);

	private static final String DEFAULT_ACCESS_KEY = "DEFAULT";

	private final PluginsMetadataCache pluginsMetadataCache;
	private final PluginsMetadataStore pluginsMetadataStore;
	private final PluginsFileManager pluginsFileManager;
	private final ConversionMessageHandler conversionHandler;
	private final PluginDescriptorsReader pluginDescriptorsReader;
	private final PluginCacheSyncTimings pluginCacheSyncTimings;
	private final Map<String, String> repoTokens;
	private final String defaultInternalReqType;

	@Autowired
	public PluginManager(
			PluginsMetadataStore pluginsMetadataStore,
			PluginsMetadataCache pluginsMetadataCache,
			PluginsFileManager pluginsFileManager,
			ConversionMessageHandler conversionHandler,
			PluginDescriptorsReader pluginDescriptorsReader,
			PluginCacheSyncTimings pluginCacheSyncTimings,
			Map<String, String> repoTokens,
			@Value("${plugins.metadata.bootstrap.plugin.default-request-type}") String defaultInternalReqType)
	{			
		this.pluginsMetadataStore = pluginsMetadataStore;
		this.pluginsMetadataCache = pluginsMetadataCache;
		this.pluginsFileManager = pluginsFileManager;
		this.pluginDescriptorsReader = pluginDescriptorsReader;
		this.conversionHandler = conversionHandler;
		this.pluginCacheSyncTimings = pluginCacheSyncTimings;
		this.repoTokens = repoTokens;
		this.defaultInternalReqType = defaultInternalReqType;
	}

	/**
	 * @param pluginsMetadataPluginDesc
	 * @return key for Plugins Metadata plugin or <code>null</code> if install failed 
	 * @throws PluginStoreAccessException
	 * @throws PluginInstallationException
	 */
	public final PluginKey installPluginsMetadataPlugin(PluginDescriptor pluginsMetadataPluginDesc) throws PluginStoreAccessException, PluginInstallationException 
	{
		Objects.requireNonNull(pluginsMetadataPluginDesc);

		PluginKey cachedPlugin = updateCacheWith(pluginsMetadataPluginDesc);
		String errMsg;

		if (cachedPlugin != null) {

			if (LOG.isDebugEnabled()) {
				Path pluginInstallLocation = pluginsFileManager.getPluginInstallLocation(pluginsMetadataPluginDesc.getKey(), pluginsMetadataPluginDesc.getHeader());
				LOG.debug("Expecting default Plugins Metadata plugin at location, {}", pluginInstallLocation.toAbsolutePath().toString());			
			}

			Set<PluginKey> installedPlugins = auditInstalledPlugins(singleton(cachedPlugin));
			installedPlugins = flagPluginsAsInstalled(installedPlugins);

			if (installedPlugins.size() == 1) {
				return installedPlugins.iterator().next();
			} else {
				errMsg = String.format(
						"Expected a single installed entry for the Plugins Metadata plugin but %d found (%s).",
						installedPlugins.size(), cachedPlugin.toString());
			}
		} else {
			errMsg = String.format("Failed to update cache with entry for the Plugins Metadata plugin (%s).",
					pluginsMetadataPluginDesc.getKey().toString());
		}

		throw new PluginInstallationException(errMsg);
	}

	/**
	 * @throws PluginStoreAccessException
	 * @throws PluginInstallationException
	 */
	public Set<PluginKey> syncPluginEnvironment() throws PluginInstallationException 
	{
		resyncCacheWithExternalStore();

		Set<PluginKey> requiredPlugins = getPlugins();
		Set<PluginKey> preInstalledPlugins = auditInstalledPlugins();
		preInstalledPlugins = flagPluginsAsInstalled(preInstalledPlugins);

		// install additionally required plugins	
		var pluginsPendingInstall = pluginMetadataRelativeComplement(requiredPlugins, preInstalledPlugins);
		Set<PluginKey> justInstalledPlugins = installPlugins(pluginsPendingInstall);

		// remove redundant plugins
		var pluginsPendingRemoval = pluginMetadataRelativeComplement(justInstalledPlugins, requiredPlugins);
		removePlugins(pluginsPendingRemoval);

		// union describing all installed plugins
		HashSet<PluginKey> installedPlugins = new HashSet<>(preInstalledPlugins);
		installedPlugins.addAll(justInstalledPlugins);
		return installedPlugins;
	}

	public void clearCache() {
		pluginsMetadataCache.clearCache();
	}

	public Set<PluginKey> flagPluginsAsInstalled(Set<PluginKey> plugins)
	{
		Objects.requireNonNull(plugins);
		Set<PluginKey> pluginsMarkedAsInstalled = new HashSet<>();

		for (PluginKey k: plugins) {
			boolean installed = pluginsMetadataCache.setInstalled(k, true);
			if (installed) {
				pluginsMarkedAsInstalled.add(k);
				LOG.debug("Successfully marked plugin, {}, as installed", k);
			} else {
				LOG.warn("Failed to mark plugin, {}, as installed", k);
			}
		}		
		return pluginsMarkedAsInstalled;
	}

	public Set<PluginKey> enablePlugins(Set<PluginKey> plugins) 
	{
		Objects.requireNonNull(plugins);
		Set<PluginKey> pluginsMarkedAsEnabled = new HashSet<>();

		for (PluginKey k: plugins) {
			boolean enabled = pluginsMetadataCache.setEnabled(k, true);
			if (enabled) {
				pluginsMarkedAsEnabled.add(k);
				LOG.debug("Successfully enabled plugin: {}", k);
			} else {
				LOG.warn("Failed to enable plugin {}", k);
			}
		}
		return pluginsMarkedAsEnabled;
	}

	protected PluginKey updateCacheWith(PluginDescriptor pluginDesc) 
	{
		if (pluginsMetadataCache.update(Set.of(pluginDesc))) {
			return pluginDesc.getKey();
		}
		return null;
	}

	/**
	 * Forces an update of the local plugin metadata cache from the external store
	 * @return plugin metadata added to the local cache during the update
	 */
	public Set<PluginHeaderDescriptor> forceCacheUpdate() 
	{
		return null;
	}

	/**
	 * Forces a clear-down of the local plugin metadata cache followed by re-sync with the external store
	 * @throws PluginStoreAccessException 
	 * @throws PluginInstallationException 
	 */
	public void resyncCacheWithExternalStore() throws PluginInstallationException 
	{			
		int intitalSyncDeplay = pluginCacheSyncTimings.getInitialSyncDeplaySec();
		int initialSyncRetry = pluginCacheSyncTimings.getInitialSyncRetrySec();

		try {
			String rawPluginsMetadata = queryPluginsMetadataExternalStore(intitalSyncDeplay, initialSyncRetry);
			Set<PluginDescriptor> pluginMetaData = convertRawPluginsMetadata(rawPluginsMetadata);

			// populate plugin metadata cache
			if (pluginsMetadataCache.update(pluginMetaData)) {
				LOG.info("Completed update of Plugins Metadata cache ({} plugin entries added)", 
						pluginMetaData.size());				
			} else {
				LOG.warn("Failed to update Plugins Metadata cache");
			}
		} catch (PluginStoreAccessException e) {
			throw new PluginInstallationException("Failed to communicate with the external plugins metadata store", e);
		}

		LOG.info("Plugins Metadata cache currently specifies a total of {} plugins",
				getPlugins().size());
	}

	/**
	 * Converts raw plugins metadata to the internal (plugin-cache-compliant) format
	 * @param rawPluginsMetadata
	 * @return
	 * @throws PluginInstallationException
	 */
	private Set<PluginDescriptor> convertRawPluginsMetadata(String rawPluginsMetadata) throws PluginInstallationException 
	{ 
		ConversionMetadata convMetaData = new ConversionMetadata.Builder(defaultInternalReqType).build();
		var convReqMsg = new ConversionMessage(rawPluginsMetadata, convMetaData);

		try {
			ConversionMessage msgResponse = conversionHandler.handle(convReqMsg);
			String convertedPluginsMetadata = msgResponse.getContent();			
			return pluginDescriptorsReader.parse(convertedPluginsMetadata);
		} catch (HandlingConfigurationException e) {
			throw new PluginInstallationException("Failed to install Plugin metadata. Likely due to a deployment configuration issue or a flaw"
					+ " in the 'Plugin metadata' plugin", e);
		} catch (MessageProcessingException | PluginConfigurationException e) {
			throw new PluginInstallationException("Failed to install Plugin metadata. Likely due to an incorrect/unrecognised response payload"
					+ " describing the supported plugins", e);					
		}
	}

	private String queryPluginsMetadataExternalStore(int syncDeplaySec, int syncRetrySec) throws PluginStoreAccessException 
	{
		Optional<String> _rawPluginsMetadata = Optional.empty();

		try {
			int attempts = 1;
			LOG.info("Starting polling of Plugins Metadata store #{} in {} seconds", attempts, syncDeplaySec);
			TimeUnit.SECONDS.sleep(syncDeplaySec);

			do {
				LOG.info("Starting polling of Plugins Metadata store #{}", attempts);
				_rawPluginsMetadata = pluginsMetadataStore.getAllPluginsMetadata();
				if (LOG.isInfoEnabled()) {
					String status = _rawPluginsMetadata.isEmpty() ? "Metadata currently unavailable" : "Metadata obtained";
					LOG.info("Finished polling of Plugins Metadata store #{}. [Response status: {}]", attempts, status);
				}
				TimeUnit.SECONDS.sleep(syncRetrySec);
				attempts++;
			} while(_rawPluginsMetadata.isEmpty());

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOG.error("THREAD INTERRUPTED: Was waiting to retry polling of the Plugins Metadata store");	// error as not expecting interrupts!
		}
		return _rawPluginsMetadata.get();
	}

	/**
	 * @return plug-ins currently installed on Converter environment
	 */
	protected Set<PluginKey> auditInstalledPlugins() 
	{	
		return auditInstalledPlugins(getPlugins());
	}

	/**
	 * @param candidatePlugins
	 * @return plug-ins that were specified and currently installed on Converter environment
	 */
	protected Set<PluginKey> auditInstalledPlugins(Set<PluginKey> candidatePlugins) 
	{
		return candidatePlugins.stream()
				.map(k -> Pair.of(k, pluginsMetadataCache.findHeaderDescriptor(k)))
				.filter(e -> pluginsFileManager.isPluginInstalled(e.getKey(), e.getValue()))
				.map(Pair::getKey)
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * @param plugins to be installed on Converter env
	 * @return plugins that were installed successfully
	 * @throws PluginInstallationException 
	 */
	protected Set<PluginKey> installPlugins(Set<PluginKey> plugins) throws PluginInstallationException 
	{
		Set<PluginKey> installedPlugins = new HashSet<>();

		for (PluginKey pluginKey : plugins) {
			PluginHeaderDescriptor pluginHeaderDesc = pluginsMetadataCache.findHeaderDescriptor(pluginKey);

			/*if (pluginHeaderDesc.isRepoAccessRestriction()) {
				String accessToken = getRepoAccessToken(pluginKey);
				pluginsFileManager.install(pluginKey, pluginHeaderDesc, accessToken);
			} else {
				pluginsFileManager.install(pluginKey, pluginHeaderDesc);
			}*/			
			pluginsFileManager.install(pluginKey, pluginHeaderDesc);
			pluginsMetadataCache.setInstalled(pluginKey, true);
			LOG.info("Successfully installed plugin: {}", pluginKey);

			installedPlugins.add(pluginKey);		
		}

		return installedPlugins;
	}

	/**
	 * @param plugins to be removed from Converter env
	 * @return plugins that were removed successfully
	 */
	protected Set<PluginKey> removePlugins(Set<PluginKey> plugins) {
		return Collections.emptySet();
	}

	/**
	 * @param requestParams
	 * @return detail needed by the plugin proxy system to carry out a message conversion 
	 */
	public Set<Pair<ConversionDescriptor, PluginKey>> getConversionMetadata(ConversionMetadata conversionRequestParams) {
		return pluginsMetadataCache.findConversions(conversionRequestParams);
	}

	protected Set<PluginKey> getPlugins() {
		return pluginsMetadataCache.findPlugins(false, false);
	}

	protected Set<PluginKey> getActivePlugins() {
		return pluginsMetadataCache.findPlugins(true, true);
	}

	protected Set<PluginKey> getInstalledPlugins() {
		return pluginsMetadataCache.findPlugins(true, false);
	}

	private Set<PluginKey> pluginMetadataRelativeComplement(Set<PluginKey> origMap, Set<PluginKey> subtractor) 
	{
		Set<PluginKey> relativeComplement = new HashSet<>(origMap);
		relativeComplement.removeAll(subtractor);
		return relativeComplement;
	}

	private String getRepoAccessToken(PluginKey pluginKey) throws PluginInstallationException 
	{
		if (repoTokens.containsKey(pluginKey.getId())) {
			return repoTokens.get(pluginKey.getId());
		} else if (repoTokens.containsKey(DEFAULT_ACCESS_KEY)) {
			return repoTokens.get(DEFAULT_ACCESS_KEY);
		} else {
			String errMsg = String.format(
					"No repo Access Token specified for plugin '%s' (%s)",
					pluginKey.getId(), pluginKey.getVersion());
			throw new PluginInstallationException(errMsg);
		}
	}

	/**
	 * @return pretty string report of all the active plugins
	 */
	public String reportActivePlugins() 
	{
		Set<PluginKey> activePlugins = getActivePlugins();
		String fmtStr = activePlugins.stream()
				.map(PluginKey::toString)
				.collect(Collectors.joining("%n    ", "%d Active Plugin(s):%n    ", ""));
		return String.format(fmtStr, activePlugins.size());
	}

}
