package org.epos.converter.common.plugin.type;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public final class ExecutionDescriptor {
	
	private String parentPluginId;
	private String parentPluginVersion;
	private PluginProxyType proxyType;	
	private String pluginInstallLoc;
	private String[] targetDetail;
	
	public ExecutionDescriptor(String pluginId, PluginProxyType proxyType, String pluginInstallLoc, String... targetDetail) 
	{
		this(pluginId, null, proxyType, pluginInstallLoc, targetDetail);
	}
	
	public ExecutionDescriptor(String pluginId, String pluginVersion, PluginProxyType proxyType, String pluginInstallLoc, String... targetDetail) 
	{
		this.parentPluginId = Objects.requireNonNull(pluginId);
		this.parentPluginVersion = pluginVersion;
		this.proxyType = Objects.requireNonNull(proxyType);		
		this.targetDetail = Objects.requireNonNull(targetDetail);	
		this.pluginInstallLoc = Paths.get(Objects.requireNonNull(pluginInstallLoc))
									.normalize().toString();
	}
		
	/**
	 * @deprecated use the new constructor as now want to set a plugin ID
	 */
	public ExecutionDescriptor(PluginProxyType proxyType, String pluginInstallLoc, String... targetDetail) {
		this.proxyType = Objects.requireNonNull(proxyType);		
		this.targetDetail = Objects.requireNonNull(targetDetail);	
		this.pluginInstallLoc = Paths.get(Objects.requireNonNull(pluginInstallLoc))
									.normalize().toString();
	}
	
	public String getParentPluginId() {
		return parentPluginId;
	}
	
	public String getParentPluginVersion() {
		return parentPluginVersion;
	}

	public PluginProxyType getProxyType() {
		return proxyType;
	}
	
	public String getPluginInstallLoc() {
		return pluginInstallLoc;
	}
	
	public String[] getTargetDetail() {
		return targetDetail;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pluginInstallLoc == null) ? 0 : pluginInstallLoc.hashCode());
		result = prime * result + ((proxyType == null) ? 0 : proxyType.hashCode());
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
		ExecutionDescriptor other = (ExecutionDescriptor) obj;
		if (pluginInstallLoc == null) {
			if (other.pluginInstallLoc != null)
				return false;
		} else if (!pluginInstallLoc.equals(other.pluginInstallLoc))
			return false;
		if (proxyType != other.proxyType)
			return false;
		if (!Arrays.equals(targetDetail, other.targetDetail))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExecutionDescriptor [parentPluginId=" + parentPluginId + ", parentPluginVersion=" + parentPluginVersion
				+ ", proxyType=" + proxyType + ", pluginInstallLoc=" + pluginInstallLoc + ", targetDetail="
				+ Arrays.toString(targetDetail) + "]";
	}
	
}
