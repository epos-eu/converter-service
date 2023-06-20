package org.epos.converter.common.schema.validation;

import java.util.Objects;
import java.util.Optional;

import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.LocationType;

public final class SchemaDescriptor {
	
	// parent's validation > format-type OR if no parent the format-type corresponding to the root's content-type 
	private ContentFormat pointerContentFormat;	
	// pointer-expression
	private Optional<String> pointerExpression;	
	// validation > format-type
	private ContentFormat contentFormat;		
	// validation > name
	private String schemaName;	
	// validation > location-type
	private LocationType schemaLocationType;
	// validation > location
	private String schemaLocation;
	
	
	/**
	 * Used if entire content is to be considered for validation. To be defaulted later to the appropriate root notation for the 
	 *  content's format type
	 */
	public SchemaDescriptor(ContentFormat contentFormat, String schemaName, LocationType schemaLocationType, 
							String schemaLocation) throws IllegalArgumentException {
		this(null, null, contentFormat, schemaName, schemaLocationType, schemaLocation);
	}
	
	/**
	 * Used if entire content is to be considered for validation. To be defaulted later to the appropriate root notation for the 
	 *  content's format type
	 */
	public SchemaDescriptor(ContentFormat pointerContentFormat, ContentFormat contentFormat,
			String schemaName, LocationType schemaLocationType, String schemaLocation) throws IllegalArgumentException {
		this(pointerContentFormat, null, contentFormat, schemaName, schemaLocationType, schemaLocation);
	}
	
	public SchemaDescriptor(ContentFormat pointerContentFormat, String pointerExpression, ContentFormat contentFormat,
			String schemaName, LocationType schemaLocationType, String schemaLocation) throws IllegalArgumentException {
		super();
		try {
			this.contentFormat = Objects.requireNonNull(contentFormat);
			this.pointerContentFormat = pointerContentFormat!=null ? pointerContentFormat : contentFormat;			
			this.pointerExpression = Optional.ofNullable(pointerExpression);
			this.schemaName = Objects.requireNonNull(schemaName);
			validateContentSchemaFile(this.schemaName, this.contentFormat);
			this.schemaLocationType = Objects.requireNonNull(schemaLocationType);
			this.schemaLocation = Objects.requireNonNull(schemaLocation);
		} catch (NullPointerException e) {
			String errMsg = String.format("Construction of '%s' instance failed due to missing mandatory arguments.", getClass().getName());
			// TODO log errMsg @ERROR level
			throw new IllegalArgumentException(errMsg, e);
		}
	}

	private void validateContentSchemaFile(String schemaFileName, ContentFormat format) throws IllegalArgumentException {
		String schemaFileExtension = "." + format.getSchemaFileExtension();
		if (!schemaFileName.endsWith(schemaFileExtension)) {
			String errMsg = String.format(
				"Inconsistency between specified target format '%s' and schema file '%s'"
					, format.name(), schemaFileName);
			throw new IllegalArgumentException(errMsg);
		}
	}

	public ContentFormat getPointerContentFormat() {
		return pointerContentFormat;
	}

	public Optional<String> getPointerExpression() {
		return pointerExpression;
	}

	public ContentFormat getContentFormat() {
		return contentFormat;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public LocationType getSchemaLocationType() {
		return schemaLocationType;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

}
