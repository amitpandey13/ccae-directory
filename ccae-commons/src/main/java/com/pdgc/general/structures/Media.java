package com.pdgc.general.structures;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * A class to describe media
 * 
 * @author Vishal Raut
 */
public class Media implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Long mediaId;
	protected String mediaName;
	protected String mediaShortName;
	
	protected Media() {} 
	
	public Media(Long mediaId, String mediaName) {
		this(mediaId, mediaName, null);
	}

	public Media(Long mediaId, String mediaName, String mediaShortName) {
		this.mediaId = mediaId;
		setMediaName(mediaName);		
		setMediaShortName(mediaShortName);
	}
	
	public Media(Media m) {
		mediaId = m.mediaId;
		mediaName = m.mediaName;
		mediaShortName = m.mediaShortName;
	}
	
	protected void setMediaName(String mediaName) {
		if (StringUtils.isBlank(mediaName)) {
			mediaName = "";
		}
		this.mediaName = mediaName;
	}
	
	protected void setMediaShortName(String mediaShortName) {
		if (StringUtils.isBlank(mediaShortName)) {
			mediaShortName = mediaName;
		}
		this.mediaShortName = mediaShortName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return mediaId.equals(((Media)obj).mediaId);
	}

	public Long getMediaId() {
		return mediaId;
	}

	public String getMediaName() {
		return mediaName;
	}

	public String getMediaShortName() {
		return mediaShortName;
	}
	
	@Override
	public int hashCode() {
		return mediaId.hashCode();
	}

	@Override
	public String toString() {
		return mediaName;
	}
}
