package com.pdgc.general.structures;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * A class to describe language
 * 
 * @author Vishal Raut
 */

public class Language implements Serializable {
	
    private static final long serialVersionUID = 1L;
	
    protected Long languageId;
	protected String languageName;
	protected String languageShortName;

	protected Language() {} 
	
	public Language(Long languageId, String languageName) {
		this(languageId, languageName, null);
	}

	public Language(Long languageId, String languageName, String languageShortName) {
		this.languageId = languageId;
		setLanguageName(languageName);
		setLanguageShortName(languageShortName);
	}

	public Language(Language l) {
		languageId = l.languageId;
		languageName = l.languageName;
		languageShortName = l.languageShortName;
	}
	
	protected void setLanguageName(String languageName) {
		if (StringUtils.isBlank(languageName)) {
			languageName = "";
		}
		this.languageName = languageName;
	}
	
	protected void setLanguageShortName(String languageShortName) {
		if (StringUtils.isBlank(languageShortName)) {
			languageShortName = languageName;
		}
		this.languageShortName = languageShortName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return languageId.equals(((Language)obj).languageId);
	}

	@Override
	public int hashCode() {
		return languageId.hashCode();
	}

	@Override
	public String toString() {
		return languageName;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public String getLanguageName() {
		return languageName;
	}

	public String getLanguageShortName() {
		return languageShortName;
	}
}
