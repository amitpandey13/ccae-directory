package com.pdgc.general.structures.result;

import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;

public class ConflictCheckInitiatorResult {
	private Map<ConflictSourceGroupKey, Map<ConflictSourceGroupKey, Set<Pair<ConflictSourceGroupKey, ConflictSourceGroupKey>>>> keyBuckets;
	private long totalKeyPairFetchTime;
	
	private int workingCopyRightsCount;
	private Long workingCopyRightsQueryTime;
	private Long workingCopyCarveOutQueryTime;
	private Long workingCopyHeapSize;
	private Long workingCopyHeapFree;	
	
	private int conflictingRightsCount;
	private int conflictingRightsDealCount;
	private Long conflictingRightsQueryTime;
	private Long conflictingCarveOutQueryTime;
	private Long conflictingHeapSize;
	private Long conflictingHeapFree;	

	private int conflictingWorkingCopyRightsCount;
	private int conflictingWorkingCopyRightsDealCount;
	private Long conflictingWorkingCopyRightsQueryTime;
	private Long conflictingWorkingCopyCarveOutQueryTime;
	private Long conflictingWorkingCopyHeapSize;
	private Long conflictingWorkingCopyHeapFree;

	
	public ConflictCheckInitiatorResult(Map<ConflictSourceGroupKey, Map<ConflictSourceGroupKey, Set<Pair<ConflictSourceGroupKey, ConflictSourceGroupKey>>>> keyPairs,
			long totalKeyPairFetchTime, int workingCopyRightsCount, Long workingCopyRightsQueryTime,
			Long workingCopyCarveOutQueryTime, Long workingCopyHeapSize, Long workingCopyHeapFree,
			int conflictingRightsCount, int conflictingRightsDealCount, Long conflictingRightsQueryTime,
			Long conflictingCarveOutQueryTime, Long conflictingHeapSize, Long conflictingHeapFree,
			int conflictingWorkingCopyRightsCount, int conflictingWorkingCopyRightsDealCount,
			Long conflictingWorkingCopyRightsQueryTime, Long conflictingWorkingCopyCarveOutQueryTime,
			Long conflictingWorkingCopyHeapSize, Long conflictingWorkingCopyHeapFree) {
		super();
		this.keyBuckets = keyPairs;
		this.totalKeyPairFetchTime = totalKeyPairFetchTime;
		this.workingCopyRightsCount = workingCopyRightsCount;
		this.workingCopyRightsQueryTime = workingCopyRightsQueryTime;
		this.workingCopyCarveOutQueryTime = workingCopyCarveOutQueryTime;
		this.workingCopyHeapSize = workingCopyHeapSize;
		this.workingCopyHeapFree = workingCopyHeapFree;
		this.conflictingRightsCount = conflictingRightsCount;
		this.conflictingRightsDealCount = conflictingRightsDealCount;
		this.conflictingRightsQueryTime = conflictingRightsQueryTime;
		this.conflictingCarveOutQueryTime = conflictingCarveOutQueryTime;
		this.conflictingHeapSize = conflictingHeapSize;
		this.conflictingHeapFree = conflictingHeapFree;
		this.conflictingWorkingCopyRightsCount = conflictingWorkingCopyRightsCount;
		this.conflictingWorkingCopyRightsDealCount = conflictingWorkingCopyRightsDealCount;
		this.conflictingWorkingCopyRightsQueryTime = conflictingWorkingCopyRightsQueryTime;
		this.conflictingWorkingCopyCarveOutQueryTime = conflictingWorkingCopyCarveOutQueryTime;
		this.conflictingWorkingCopyHeapSize = conflictingWorkingCopyHeapSize;
		this.conflictingWorkingCopyHeapFree = conflictingWorkingCopyHeapFree;
	}


	public Map<ConflictSourceGroupKey, Map<ConflictSourceGroupKey, Set<Pair<ConflictSourceGroupKey, ConflictSourceGroupKey>>>> getKeyBuckets() {
		return keyBuckets;
	}


	public long getTotalKeyPairFetchTime() {
		return totalKeyPairFetchTime;
	}


	public int getWorkingCopyRightsCount() {
		return workingCopyRightsCount;
	}


	public Long getWorkingCopyRightsQueryTime() {
		return workingCopyRightsQueryTime;
	}


	public Long getWorkingCopyCarveOutQueryTime() {
		return workingCopyCarveOutQueryTime;
	}


	public Long getWorkingCopyHeapSize() {
		return workingCopyHeapSize;
	}


	public Long getWorkingCopyHeapFree() {
		return workingCopyHeapFree;
	}


	public int getConflictingRightsCount() {
		return conflictingRightsCount;
	}


	public int getConflictingRightsDealCount() {
		return conflictingRightsDealCount;
	}


	public Long getConflictingRightsQueryTime() {
		return conflictingRightsQueryTime;
	}


	public Long getConflictingCarveOutQueryTime() {
		return conflictingCarveOutQueryTime;
	}


	public Long getConflictingHeapSize() {
		return conflictingHeapSize;
	}


	public Long getConflictingHeapFree() {
		return conflictingHeapFree;
	}


	public int getConflictingWorkingCopyRightsCount() {
		return conflictingWorkingCopyRightsCount;
	}


	public int getConflictingWorkingCopyRightsDealCount() {
		return conflictingWorkingCopyRightsDealCount;
	}


	public Long getConflictingWorkingCopyRightsQueryTime() {
		return conflictingWorkingCopyRightsQueryTime;
	}


	public Long getConflictingWorkingCopyCarveOutQueryTime() {
		return conflictingWorkingCopyCarveOutQueryTime;
	}


	public Long getConflictingWorkingCopyHeapSize() {
		return conflictingWorkingCopyHeapSize;
	}


	public Long getConflictingWorkingCopyHeapFree() {
		return conflictingWorkingCopyHeapFree;
	}	

	
	
	
}



