package org.epos.converter.common.type;

import java.net.URL;
import java.util.Optional;

import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentExtractionException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;

/**
 * TODO consider getting rid of the class generics T
 */
public abstract class ContentOperationSet<T> {
	
	/**
	 * Extracts a portion of the content based on some agreed document pointer format for the given {@link ContentFormat} 
	 * (e.g. XPath for XML content; JSONPath for JSON content)  
	 */
	public abstract String getSubstrFromContent(String pointerExpr, String content) throws ContentExtractionException;
	
	public abstract String getRootPointerExpr();
	
	public abstract ContentFormat getSupportedContentFormat();
		
	public abstract Optional<Object> loadSchema(URL schemaPathUrl) throws ConfigurationException;
	
	protected abstract void doValidateContent(T typedSchema, String contentSubstr) throws ContentValidationException;

	protected abstract Class<T> getSchemaClass();
		
	public void validateContent(Object schema, String content) throws ContentValidationException {
		T typedSchema = getSchemaClass().cast(schema);	
		doValidateContent(typedSchema, content);
	}
		
	public String getAsCleanContent(String str) {
		return str;
	}

}
