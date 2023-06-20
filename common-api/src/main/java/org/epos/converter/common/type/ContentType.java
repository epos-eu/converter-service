package org.epos.converter.common.type;

import static org.epos.converter.common.type.ContentFormat.JSON;
import static org.epos.converter.common.type.ContentFormat.XML;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.epos.converter.common.type.ContentFormat;

public enum ContentType {

	EPOS_GEO_JSON("application/epos.geo+json", JSON),
	EPOS_ONLY_TABLE_GEO_JSON("application/epos.table.geo+json", JSON),
	EPOS_ONLY_MAP_GEO_JSON("application/epos.map.geo+json", JSON),
	EPOS_COVERAGE_JSON("application/epos.coverage+json", JSON),
	EPOS_RESULT_SET("application/epos.result", JSON),
	EPOS_PLAIN_JSON("application/epos.plain+json", JSON),
	APPLICATION_COV_JSON("application/covjson", JSON),
	APPLICATION_SIMPLE_COV_JSON("covjson", JSON),
	APPLICATION_JSON("application/json", JSON),
	APPLICATION_XML("application/xml", XML);

	private String value;
	private ContentFormat contentFormat;

	private ContentType(String value, ContentFormat contentFormat) {
		this.value = value;
		this.contentFormat = contentFormat;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	public ContentFormat getContentFormat() {
		return contentFormat;
	}
	
	public static Optional<ContentType> fromValue(String value) {
		return StringUtils.isBlank(value) ? Optional.empty() : 
					Arrays.stream(ContentType.values())
						.filter(v -> !Objects.isNull(v))
						.filter(v -> StringUtils.isNotBlank(v.getValue()) && v.getValue().equals(value))
						.findAny();
	}
	
	public static String prettyPrintSupportedValues() {
		return Arrays.stream(ContentType.values())
			.map(ContentType::getValue)
			.collect(Collectors.joining("; ", "[", "]"));
	}

}
