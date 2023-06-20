package org.epos.converter.app.plugin.managment.configuration;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.epos.router_framework.domain.BuiltInActorType.CONVERTER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.epos.converter.app.configuration.properties.PluginsMetadataBootstrapPluginProperties;
import org.epos.converter.app.configuration.properties.RoutingFrameworkProperties;
import org.epos.converter.app.plugin.managment.PluginManager;
import org.epos.converter.app.plugin.managment.exception.PluginInstallationException;
import org.epos.converter.app.plugin.managment.exception.PluginStoreAccessException;
import org.epos.converter.app.plugin.managment.model.ConversionDescriptor;
import org.epos.converter.app.plugin.managment.model.ExecutionDescriptor;
import org.epos.converter.app.plugin.managment.model.MappingDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.epos.converter.app.router.RouterInitializer;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.router_framework.RpcRouter;
import org.epos.router_framework.RpcRouterBuilder;
import org.epos.router_framework.domain.Actor;
import org.epos.router_framework.domain.Request;
import org.epos.router_framework.domain.Response;
import org.epos.router_framework.exception.RoutingException;
import org.epos.router_framework.types.PayloadType;
import org.epos.router_framework.types.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(PluginsMetadataBootstrapPluginProperties.class)
public class PluginStoreConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(PluginManager.class);
	private static final Actor THIS_COMPONENT = Actor.getInstance(CONVERTER);
	private static final String REPO_TOKEN_KEY_PREFIX = "REPOTOKEN_";
	
	private final RouterInitializer routerInitializer;
	
	@Autowired
	public PluginStoreConfiguration(RouterInitializer routerInitializer) {			
		this.routerInitializer = routerInitializer;
	}
	
	@Bean
	public Map<String, String> repoTokens() 
	{
		return System.getenv().entrySet().stream()
			.filter(e -> e.getKey() instanceof String)
			.filter(e -> e.getValue() instanceof String)
			.filter(e -> e.getKey().toString().startsWith(REPO_TOKEN_KEY_PREFIX))
			.collect(toUnmodifiableMap(
					e -> StringUtils.removeStart(e.getKey().toString(), REPO_TOKEN_KEY_PREFIX),
					e -> e.getValue().toString()));
	}
	
	@Bean
	@Profile({ "prod", "dev" })
	public CommandLineRunner pluginsStoreCommandLineRunner(ApplicationContext ctx, RpcRouter pluginStoreRpcRouter,
			PluginManager pluginManager, PluginsMetadataBootstrapPluginProperties pluginsMetadataBootstrapPluginProps) 
	{
		return args -> {
			
			// Initialise router used for querying Plug-in Store 
			routerInitializer.initRouter(pluginStoreRpcRouter);
			
	 		if (LOG.isInfoEnabled()) {
	 			String infoMsgFmt = "Router initialisation completed for querying Plugin Store ['%s' component]: Ready to communicate with message bus.";
	 			LOG.info(String.format(infoMsgFmt, THIS_COMPONENT.name()));	 			
	 		}
	 		
	 		// Initialise Converter's plugin store cache
			pluginStoreInitialisation(pluginManager, pluginsMetadataBootstrapPluginProps);
		};
	}
	
	@Bean
	@Profile({ "test" })
	public CommandLineRunner mockPluginsStoreCommandLineRunner(ApplicationContext ctx,
			PluginManager pluginManager, PluginsMetadataBootstrapPluginProperties pluginsMetadataBootstrapPluginProps) 
	{
		return args -> {
			
			// No initialisation of router required for handling Plug-in Management requests when in test mode.
			if (LOG.isInfoEnabled()) {
				String infoMsgFmt = "No router initialisation for querying of Plugin Store required when in test mode ['%s' component].";
				LOG.info(String.format(infoMsgFmt, THIS_COMPONENT.name()));				
			}
			
	 		// Initialise Converter's plugin store cache
			pluginStoreInitialisation(pluginManager, pluginsMetadataBootstrapPluginProps);
		};
	}
	
	@Bean
	@ConfigurationProperties("plugins-cache.plugin-store-syncing.routing-framework.rpc")	
	public RoutingFrameworkProperties pluginStoreMessageRoutingProperties() {
		return new RoutingFrameworkProperties();
	}
    
	@Profile({ "prod" })
	@Bean
	public RpcRouter pluginStoreRpcRouter(RoutingFrameworkProperties pluginStoreMessageRoutingProperties)
	{
		ServiceType serviceType = pluginStoreMessageRoutingProperties.getServiceType();
		Actor defaultNextComponent = pluginStoreMessageRoutingProperties.getDefaultNextComponent();
		int numOfConsumers = pluginStoreMessageRoutingProperties.getNumOfConsumers();
		int numOfProducers = pluginStoreMessageRoutingProperties.getNumOfPublishers();
		
	    Optional<RpcRouter> router = RpcRouterBuilder.instance(THIS_COMPONENT)
				.addServiceSupport(serviceType, defaultNextComponent)
				.setRoutingKeyPrefix("conv")
				.setNumberOfConsumers(numOfConsumers)
				.setNumberOfPublishers(numOfProducers)
				.build();
	    
	    return router.orElseThrow(() -> new BeanInitializationException(
	    		"Converter component's RPC Router instance for querying Plugin metadata could not be instantiated"));
	}
	
	@Profile({ "test" })
	@Bean
	public RpcRouter pluginStoreMockTestRpcRouter()
	{	   
	    return new RpcRouter() {

			@Override
			public void init(String host, String vHost, String userName, String password) throws RoutingException {
				// NO IMPLEMENTATION REQUIRED
			}

			@Override
			public Set<ServiceType> getSupportedServices() {
				// NO IMPLEMENTATION REQUIRED
				return null;
			}

			@Override
			public Response makeRequest(Request request) 
			{		
				try {
					TestResourcesAccessor resourceAccessor = new TestResourcesAccessor(Path.of("icsc","pluginsmetadata"));
					Path resourcePath = resourceAccessor.getResourcePath(Path.of("valid", "incoming_payloads", "icsc-plugins-metadata_latest.json"));
					String respPayload = Files.readString(resourcePath);
					 
					return new Response(Optional.of(respPayload), PayloadType.PLAIN_TEXT, Map.of(), "faked");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return null;
			}

			@Override
			public Response makeRequest(Request request, Actor nextComponentOverride) {
				// NO IMPLEMENTATION REQUIRED
				return null;
			}

			@Override
			public Boolean doHealthCheck() throws RoutingException {
				// TODO Auto-generated method stub
				return null;
			}
	    	
	    };
	}
	
	@Profile({ "dev" })
	@Bean
	public RpcRouter pluginStoreMockDevRpcRouter()
	{	   
	    return new RpcRouter() {

			@Override
			public void init(String host, String vHost, String userName, String password) throws RoutingException {
				// NO IMPLEMENTATION REQUIRED
			}

			@Override
			public Set<ServiceType> getSupportedServices() {
				// NO IMPLEMENTATION REQUIRED
				return Collections.emptySet();
			}

			@Override
			public Response makeRequest(Request request) 
			{		
				try {
					InputStreamReader inStream = new InputStreamReader(
							getClass().getClassLoader().getResourceAsStream("tmp_dev/icsc-plugins-metadata_latest.json"), 							
							StandardCharsets.UTF_8);
					
					try (BufferedReader br = new BufferedReader(inStream)) {
						String respPayload = br.lines().collect(Collectors.joining(System.lineSeparator()));
						return new Response(Optional.of(respPayload), PayloadType.PLAIN_TEXT, Map.of(), "faked");
					}
	
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return null;
			}

			@Override
			public Response makeRequest(Request request, Actor nextComponentOverride) {
				// NO IMPLEMENTATION REQUIRED
				return null;
			}

			@Override
			public Boolean doHealthCheck() throws RoutingException {
				// TODO Auto-generated method stub
				return null;
			}
	    	
	    };
	}

	private void pluginStoreInitialisation(PluginManager pluginManager,
			PluginsMetadataBootstrapPluginProperties pluginsMetadataBootstrapPluginProps)
			throws PluginConfigurationException, PluginInstallationException {
		/* 
		 * seed MemPluginsMetadataCache cache with plug-in metadata entry for the pre-installed plug-ins metadata plugin.
		 * A later step may check whether the latest version of this plug-in is installed 
		 * and if not, install it from some specified location.
		 */
		PluginDescriptor pluginsMetadataBootstrapPlugin = createPluginsMetadataPluginDesciptor(pluginsMetadataBootstrapPluginProps);
		try {
			// install and enable the default plugins metadata plugin
			PluginKey pluginsMetadataPlugin = pluginManager.installPluginsMetadataPlugin(pluginsMetadataBootstrapPlugin);
			pluginManager.enablePlugins(Collections.singleton(pluginsMetadataPlugin));

			Set<PluginKey> installedPlugins = pluginManager.syncPluginEnvironment();
			// enable all plugins that have been installed during the startup sync

			pluginManager.enablePlugins(installedPlugins);
			
			LOG.info(pluginManager.reportActivePlugins());
		} catch (PluginStoreAccessException ex) {
			throw new PluginConfigurationException("Failed to obtain plugins metadata from external store", ex);
		}
	}

	private static PluginDescriptor createPluginsMetadataPluginDesciptor(PluginsMetadataBootstrapPluginProperties pluginsMetadataBootstrapPlugin) 
	{
		String pluginId = pluginsMetadataBootstrapPlugin.getId();
		String pluginVer = pluginsMetadataBootstrapPlugin.getVersion();		
		String[] repoArtifactLocations = pluginsMetadataBootstrapPlugin.getInstalledArtifactsRelativeLocations();

		PluginHeaderDescriptor pluginHeader = new PluginHeaderDescriptor.Builder(
				getInstallLocation(pluginId, pluginVer),
				PluginProxyType.JAVA_REFLECTION.getId(),
				Arrays.asList(repoArtifactLocations)).build();
		
		MappingDescriptor mappingDesc = new MappingDescriptor.Builder(pluginsMetadataBootstrapPlugin.getDefaultRequestType())
			.withRequestContentType(pluginsMetadataBootstrapPlugin.getRequestContentType().getValue())
			.withResponseContentType(pluginsMetadataBootstrapPlugin.getResponseContentType().getValue())
			.build();
		
		String[] invocationDetail = pluginsMetadataBootstrapPlugin.getDefaultInvocationDetail();
		ExecutionDescriptor executionDesc = new ExecutionDescriptor(invocationDetail);
		
		PluginKey pluginKey = new PluginKey(pluginId, pluginVer);
		ConversionDescriptor conversionDesc = new ConversionDescriptor.Builder(mappingDesc, executionDesc).build();
		return new PluginDescriptor(pluginKey, pluginHeader, List.of(conversionDesc));
	}

	private static String getInstallLocation(String pluginKeyId, String pluginVer) 
	{
		String pluginIdDirName = replaceFileSeparatorChars(pluginKeyId, "-");
		String pluginKeyVerDirName = replaceFileSeparatorChars(pluginVer, "-");
		return pluginIdDirName + "/" + pluginKeyVerDirName;
	}
	
	private static String replaceFileSeparatorChars(String text, String replacement) {
		return text.replaceAll("[\\\\|/]+", replacement);
	}

}
