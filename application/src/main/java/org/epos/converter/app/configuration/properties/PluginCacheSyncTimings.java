package org.epos.converter.app.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("plugins-cache.plugin-store-syncing.sync-timings")
public final class PluginCacheSyncTimings {
	
	private final int initialSyncDeplaySec;
	private final int initialSyncRetrySec;
	private final int ongoingSyncFrequencyMin;
	private final int ongoingSyncFrequencyRetryMin;
	
	public PluginCacheSyncTimings(int initialSyncDeplaySec, int initialSyncRetrySec,
			int ongoingSyncFrequencyMin, int ongoingSyncFrequencyRetryMin) 
	{
		this.initialSyncDeplaySec = initialSyncDeplaySec;
		this.initialSyncRetrySec = initialSyncRetrySec;
		this.ongoingSyncFrequencyMin = ongoingSyncFrequencyMin;
		this.ongoingSyncFrequencyRetryMin = ongoingSyncFrequencyRetryMin;
	}

	public int getInitialSyncDeplaySec() {
		return initialSyncDeplaySec;
	}

	public int getInitialSyncRetrySec() {
		return initialSyncRetrySec;
	}

	public int getOngoingSyncFrequencyMin() {
		return ongoingSyncFrequencyMin;
	}

	public int getOngoingSyncFrequencyRetryMin() {
		return ongoingSyncFrequencyRetryMin;
	}

}
