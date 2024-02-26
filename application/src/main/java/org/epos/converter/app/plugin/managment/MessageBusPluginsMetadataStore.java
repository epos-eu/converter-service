package org.epos.converter.app.plugin.managment;

import static lombok.Lombok.sneakyThrow;

import java.util.Map;
import java.util.Optional;

import org.epos.converter.app.plugin.core.Utils;
import org.epos.converter.app.plugin.managment.exception.PluginStoreAccessException;
import org.epos.router_framework.RpcRouter;
import org.epos.router_framework.domain.Request;
import org.epos.router_framework.domain.RequestBuilder;
import org.epos.router_framework.types.ServiceType;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;

@Repository
public class MessageBusPluginsMetadataStore implements PluginsMetadataStore {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageBusPluginsMetadataStore.class);
	
	private final RpcRouter pluginStoreRpcRouter;	
	
	@Autowired
	public MessageBusPluginsMetadataStore(RpcRouter pluginStoreRpcRouter) 
	{
		this.pluginStoreRpcRouter = pluginStoreRpcRouter;
	}

	@Override
	public Optional<String> getAllPluginsMetadata() throws PluginStoreAccessException
	{
		Map<String, Object> headers = Map.of("kind", "converter");
		String requestPayload = "{ \"type\" : \"plugins\" }";
		
		// Query DataMetadataService for all plug-in metadata
		Request pluginStoreReq = RequestBuilder.instance(ServiceType.METADATA, "get", "converter-plugins")
				.addPayloadPlainText(requestPayload)
				.addHeaders(headers)
				.build();
		
		//Response pluginStoreResp = pluginStoreRpcRouter.makeRequest(pluginStoreReq);
		JsonArray pluginList = PluginGeneration.generate();
		
		if(pluginList.isEmpty() || pluginList.isJsonNull()) {
			String errMsg = String.format("Failed to obtain metadata from external plugin store");
			throw sneakyThrow(new PluginStoreAccessException(errMsg));
		}
		
		// Throw PluginStoreAccessException if error response
		/*pluginStoreResp.getErrorCode().ifPresent(code -> {			
			String errMsg = String.format("Failed to obtain metadata from external plugin store: [Routing errror code: %s]", code.getLabel());
			if (pluginStoreResp.getErrorMessage().isPresent()) {
				errMsg += "%n   " + pluginStoreResp.getErrorMessage().get();
			}
			throw sneakyThrow(new PluginStoreAccessException(errMsg));
		});*/
		
		// Return payload as JSONArray String, if valid, unless JSONArry is empty in which case return empty Optional
		// If payload does not represent a valid JSONArray then throw PluginStoreAccessException
		return Optional.of(Utils.gson.toJson(pluginList)).flatMap(payload -> {
			
			if (LOG.isDebugEnabled()) {
				String payloadResp = String.format("Plugin Store response payload...>>>%n%s%n<<<", payload);
				LOG.debug(payloadResp);
			}
			
			try {
				JSONArray jsonPayload = new JSONArray(payload);
				return jsonPayload.isEmpty() ? Optional.empty() : Optional.of(payload);				
			} catch (JSONException ex) {
				var wrappedEx = new PluginStoreAccessException("Failed to parse the JSON describing the metadata from external plugin store", ex);
				throw sneakyThrow(wrappedEx);
			}

		});
	}

}
