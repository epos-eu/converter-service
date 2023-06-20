package org.epos.converter.common.schema.validation;

import java.util.Map;
import java.util.function.Supplier;

import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;


public class DefaultContentOperationSetFactory implements ContentOperationSetFactory {
		
	private Map<ContentFormat, Supplier<ContentOperationSet<?>>> factory = Map.of(
		ContentFormat.JSON, DefaultJsonContentOpertations::new,
		ContentFormat.XML, DefaultXmlContentOpertations::new
	);
	

	@Override
	public ContentOperationSet<?> getInstance(ContentFormat contentFormat) throws UnsupportedOperationException {
		
		Supplier<ContentOperationSet<?>> supplier = factory.get(contentFormat);
		ContentOperationSet<?> contentOperationSet = supplier.get();
		
		if (contentOperationSet == null) {
			throw new UnsupportedOperationException(String.format(
					"There is no supported Content Operations for the content format, %s", 
					contentFormat.getName()));
		}
		return contentOperationSet;
	}

}
