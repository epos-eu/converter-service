package org.epos.converter.common.type;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public enum LocationType {
	
	PATH("path"),
	URL("url");

	final String name;
	
	private LocationType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Optional<LocationType> getInstance(String id) {
		return StringUtils.isBlank(id) ? Optional.empty() : 
					Arrays.stream(LocationType.values())
						.filter(v -> StringUtils.isNotBlank(v.getName()) && v.getName().equals(id))
						.findAny();
	}

}
