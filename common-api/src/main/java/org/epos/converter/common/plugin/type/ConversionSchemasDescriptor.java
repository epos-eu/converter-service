package org.epos.converter.common.plugin.type;

import org.epos.converter.common.schema.validation.SchemaDescriptorTree;

public class ConversionSchemasDescriptor {
	
	private SchemaDescriptorTree requestRootSchema;
	private SchemaDescriptorTree responseRootSchema;
	
	public ConversionSchemasDescriptor(SchemaDescriptorTree requestSchema, SchemaDescriptorTree responseSchema) {
		super();
		this.requestRootSchema = requestSchema;
		this.responseRootSchema = responseSchema;
	}

	public SchemaDescriptorTree getRequestRootSchema() {
		return requestRootSchema;
	}

	public SchemaDescriptorTree getResponseRootSchema() {
		return responseRootSchema;
	}

}
