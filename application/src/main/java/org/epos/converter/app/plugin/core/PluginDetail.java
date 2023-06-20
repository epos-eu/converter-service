package org.epos.converter.app.plugin.core;

import java.util.Arrays;

import org.epos.converter.common.plugin.type.PluginProxyType;

/**
 * @deprecated replaced by PluginHeader structure!
 */
public class PluginDetail {
	
	private String name, version;
	private PluginProxyType invokerType;
	private String[] targetDetail;
	
	public PluginDetail(String name, String version, PluginProxyType invokerType, String... targetDetail) {
		super();
		this.name = name;
		this.version = version;
		this.invokerType = invokerType;
		this.targetDetail = targetDetail;
	}

	public PluginProxyType getInvokerType() {
		return invokerType;
	}

	public String[] getTargetDetail() {
		return targetDetail;
	}

	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((invokerType == null) ? 0 : invokerType.hashCode());
		result = prime * result + Arrays.hashCode(targetDetail);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluginDetail other = (PluginDetail) obj;
		if (invokerType != other.invokerType)
			return false;
		if (!Arrays.equals(targetDetail, other.targetDetail))
			return false;
		return true;
	}

}
