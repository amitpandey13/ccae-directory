package com.pdgc.avails.structures.criteria.export;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import lombok.Builder;

@Builder
public class ExcelTabRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ExcelTabType tabType;
	private String tabName;
	private String fileName;
	private String directoryName;
	private int finalSheetId;
	
	private Map<ExcelTabRequestParams, Object> requestParams;
	
	public ExcelTabRequest(
		ExcelTabType tabType,
		String tabName,
		String fileName,
		String directoryName,
		int finalSheetId,
		Map<ExcelTabRequestParams, Object> requestParams
	) {
		this.tabType = tabType;
		this.tabName = tabName;
		this.fileName = fileName;
		this.directoryName = directoryName;
		this.finalSheetId = finalSheetId;
		
		this.requestParams = Collections.unmodifiableMap(requestParams);
	}
	
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return tabName.equals(((ExcelTabRequest)obj).tabName);
	}
	
	public ExcelTabType getTabType() {
		return tabType;
	}
	
	public String getTabName() {
		return tabName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	public int getFinalSheetId() {
		return finalSheetId;
	}
	
	public Map<ExcelTabRequestParams, Object> getRequestParams() {
		return requestParams;
	}
}
