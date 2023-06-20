package org.epos.converter.common.plugin.type;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

public enum PluginProxyType {
	
	JAVA_REFLECTION("Java-Reflection"),
	JAVA_RMI("Java-RMI"),
	JAVA_JINI("Java-JINI"),	
	PYTHON("Python"),
	GO("Go");
	
	private String id;
	
	private PluginProxyType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public static Optional<PluginProxyType> getInstance(String id) {
		return StringUtils.isBlank(id) ? Optional.empty() : 
					Arrays.stream(PluginProxyType.values())
						.filter(v -> StringUtils.isNotBlank(v.getId()) && v.id.equals(id))
						.findAny();
	}
	
	public static String prettyPrintSupportedIds() {
		return Arrays.stream(PluginProxyType.values())
			.map(PluginProxyType::getId)
			.collect(Collectors.joining("; ", "[", "]"));
	}
}
