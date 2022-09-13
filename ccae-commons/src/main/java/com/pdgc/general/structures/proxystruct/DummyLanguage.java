package com.pdgc.general.structures.proxystruct;

import java.util.Objects;

import com.pdgc.general.structures.Language;

/**
 * Used for creating 'fake' medias that may use program-generated ids,
 * which may end up clashing with ids used by real medias from the db...
 * so this overrides the equals() method such that it only returns true 
 * using a reference equals 
 *  
 * @author Linda Xu
 *
 */
public class DummyLanguage extends Language {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DummyLanguage() {
		super();
		this.languageName = "dummyLanguage";
		this.languageShortName = "dummyLanguageShortName";
	}
	
	public DummyLanguage(String customName) {
		super();
		this.setCustomName(customName);
	}
	
	public DummyLanguage(Language language) {
		super(language);
	}
	
	/**
	 * Sets both the long and short names to the specified string
	 * The individual set methods are exposed if the user wants different long and short names
	 * @param customName
	 */
	public void setCustomName(String customName) {
		setLanguageName(customName);
		setLanguageShortName(customName);
	}
	
	@Override
	public void setLanguageName(String languageName) {
		super.setLanguageName(languageName);
	}
	
	@Override
	public void setLanguageShortName(String languageShortName) {
		super.setLanguageShortName(languageShortName);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(languageId);
	}
}
