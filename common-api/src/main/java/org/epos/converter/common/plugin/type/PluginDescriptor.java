package org.epos.converter.common.plugin.type;

import java.util.Map;

/**
 * @deprecated old DO - closest replacement probably {@link org.epos.converter.app.plugin.managment.model.PluginDescriptor}
 */
public final class PluginDescriptor {

	private Map<ConversionDescriptor, ConversionSchemasDescriptor> conversions;
	
	private HeaderDescriptor header;
		
	public PluginDescriptor(Map<ConversionDescriptor, ConversionSchemasDescriptor> conversions,
			HeaderDescriptor header) {
		super();
		this.conversions = conversions;
		this.header = header;
	}

	public Map<ConversionDescriptor, ConversionSchemasDescriptor> getConversions() {
		return conversions;
	}

	public HeaderDescriptor getHeader() {
		return header;
	}

}
