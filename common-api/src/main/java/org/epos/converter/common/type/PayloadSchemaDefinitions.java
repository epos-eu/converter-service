package org.epos.converter.common.type;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.type.ContentFormat;

/**
 * Represents the schema definitions for a given {@link org.epos.converter.common.plugin.type.ConversionDescriptor}
 *
 */
public final class PayloadSchemaDefinitions {
	
	private final Map<PayloadFormatDescriptor, String> inPayloadSchemas;
	private final Map<PayloadFormatDescriptor, String> outPayloadSchemas;
	
	private PayloadSchemaDefinitions(Map<PayloadFormatDescriptor, String> inPayloadSchemas,
			Map<PayloadFormatDescriptor, String> outPayloadSchemas) {
		super();
		this.inPayloadSchemas = inPayloadSchemas;
		this.outPayloadSchemas = outPayloadSchemas;
	}
	
	public Map<PayloadFormatDescriptor, String> getInPayloadSchemas() {
		return inPayloadSchemas;
	}

	public Map<PayloadFormatDescriptor, String> getOutPayloadSchemas() {
		return outPayloadSchemas;
	}
	
	public Set<ContentFormat> getRequiredPayloadFormats() {
		Stream<PayloadFormatDescriptor> in = inPayloadSchemas.keySet().stream();
		Stream<PayloadFormatDescriptor> out = outPayloadSchemas.keySet().stream();
		return Stream.concat(in, out)
			.map(descriptor -> {
				Set<ContentFormat> payloadFormats = new HashSet<>();
				payloadFormats.add(descriptor.getTargetFormat());
				descriptor.getPointerFormat().ifPresent(pf -> payloadFormats.add(pf));
				return payloadFormats;
			})
			.flatMap(Set::stream)
			.collect(Collectors.toSet());
	}
	
	@Override
	public String toString() {
		return "PayloadSchemaDefinitions [inPayloadSchemas=" + inPayloadSchemas + ", outPayloadSchemas="
				+ outPayloadSchemas + ", toString()=" + super.toString() + "]";
	}
	
	// ---------------------------------------- <<<<< BUILDER >>>>> ----------------------------------------
	
	public static class PayloadSchemaDefinitionsBuilder {
		
		private Map<PayloadFormatDescriptor, String> inPayloadSchemas = new LinkedHashMap<>();
		private Map<PayloadFormatDescriptor, String> outPayloadSchemas = new LinkedHashMap<>();
		
		private boolean enforceDefinitionOfSchemas = Boolean.getBoolean(System.getProperty("ENFORCE_DEFINITION_OF_SCHEMA", "false"));
		
		public static PayloadSchemaDefinitionsBuilder getInstance() {
			return new PayloadSchemaDefinitionsBuilder();
		}

		private PayloadSchemaDefinitionsBuilder() {
			super();
		}
		
		public PayloadSchemaDefinitionsBuilder incomingSchema(PayloadFormatDescriptor descriptor, String fileName) throws IllegalArgumentException {
			
			if (descriptor == null || fileName == null)  {
				String errMsg = String.format("Invalid incoming schema definition defined [%s : %s]", descriptor.toString(), fileName);
				throw new IllegalArgumentException(errMsg);
			}
			
			inPayloadSchemas.put(descriptor, fileName);
			return this;
		}
		
		public PayloadSchemaDefinitionsBuilder outgoingSchema(PayloadFormatDescriptor descriptor, String fileName) throws IllegalArgumentException {			

			if (descriptor == null || fileName == null)  {
				String errMsg = String.format("Invalid outgoing schema definition defined [%s : %s]", descriptor.toString(), fileName);
				throw new IllegalArgumentException(errMsg);
			}
			
			outPayloadSchemas.put(descriptor, fileName);
			return this;
		}
		
/*		private void checkSchemaDecsriptorArgumentPermitted(Map<PayloadFormatDescriptor, String> payloadSchemas, PayloadFormatDescriptor descriptor, String fileName) {
			if (descriptor == null || fileName == null)  {
				String errMsg = String.format("Invalid schema definition defined [%s : %s]", descriptor.toString(), fileName);
				throw new IllegalArgumentException(errMsg);
			}
		}*/
		
		public PayloadSchemaDefinitions build() throws ConfigurationException {
			
			if (enforceDefinitionOfSchemas) {
				try {
					validateSchemaDefinitions(inPayloadSchemas.keySet());
				} catch (IllegalArgumentException e) {
					throw new ConfigurationException("Failed to instantiate incoming schema definitions for payload due to invalid configuration", e);
				}
				
				try {
					validateSchemaDefinitions(outPayloadSchemas.keySet());
				} catch (IllegalArgumentException e) {
					throw new ConfigurationException("Failed to instantiate outgoing schema definitions for payload due to invalid configuration", e);
				}
			}
			
			return new PayloadSchemaDefinitions(inPayloadSchemas, outPayloadSchemas);
		}

		private void validateSchemaDefinitions(Set<PayloadFormatDescriptor> payloadSchemaDescriptors) throws IllegalArgumentException {
			
			if (payloadSchemaDescriptors.size() < 1) {
				throw new IllegalArgumentException("Expected at least 1 schema to be defined for payloads");				
			}
		
			if (!payloadSchemaDescriptors.iterator().next().isRootDescriptor()) {
				throw new IllegalArgumentException("The first schema definition declared must be a root definition");
			}
			
			if (payloadSchemaDescriptors.stream().skip(1)
					.anyMatch(PayloadFormatDescriptor::isRootDescriptor)) {
				throw new IllegalArgumentException("A root schema definition can only be declared as the first schema definition");
			}

		}
		
	}	

}
