package org.epos.converter.common.java.type;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

/**
 * To provide explicit declaration for those transformations supported by the, known (native),
 *  Java plug-ins.  
 */
public enum ConversionType {
	
//	KEYWORDS("Keywords", "Keywords Metadata"),
//	VISUALISE_RADON_TCSJSON("visualise/radon/tcsjson", "WP09 Radon: Radon Time Series");	
//	VISUALISE_STATION_TCSXML("visualise/station/fsdn-xml", "FDSN Station XML"),
//	VISUALISE_STATION_TCSJSON("visualise/station/wp10-json", "WP10 Station");
	
/*	private String id;
	private String name;
	
	private ConversionType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static Optional<ConversionType> getInstance(String id) {
		return StringUtils.isBlank(id) ? Optional.empty() : 
					Arrays.stream(ConversionType.values())
						.filter(v -> StringUtils.isNotBlank(v.getId()) && v.getId().equals(id))
						.findAny();
	}
	*/
}
