package org.epos.converter.common.schema.validation;

import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;

public interface ContentOperationSetFactory {
	
	public ContentOperationSet<?> getInstance(ContentFormat contentFormat) throws UnsupportedOperationException;

}
