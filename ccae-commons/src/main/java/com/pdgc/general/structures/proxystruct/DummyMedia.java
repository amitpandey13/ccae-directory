package com.pdgc.general.structures.proxystruct;

import java.util.Objects;

import com.pdgc.general.structures.Media;

/**
 * Used for creating 'fake' medias that may use program-generated ids,
 * which may end up clashing with ids used by real medias from the db...
 * so this overrides the equals() method such that it only returns true 
 * using a reference equals 
 *  
 * @author Linda Xu
 *
 */
public class DummyMedia extends Media {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DummyMedia() {
		this.mediaName = "dummyMedia";
		this.mediaShortName = "dummyMedia";
	}
	
	public DummyMedia(String mediaName) {
		setCustomName(mediaName);
	}
	
	public DummyMedia(Media media) {
		super(media);
	}
	
	/**
	 * Sets both the long and short names to the specified string
	 * The individual set methods are exposed if the user wants different long and short names
	 * @param customName
	 */
	public void setCustomName(String customName) {
		setMediaName(customName);
		setMediaShortName(customName);
	}
	
	@Override
	public void setMediaName(String mediaName) {
		super.setMediaName(mediaName);
	}
	
	@Override
	public void setMediaShortName(String mediaShortName) {
		super.setMediaShortName(mediaShortName);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(mediaId);
	}
}
