package com.pdgc.general.structures.customer;

import java.io.Serializable;

/**
 * Describes a Customer Genre
 * 
 * @author gowtham
 */
public class CustomerGenre implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long genreId;
	private String genreName;

	public CustomerGenre(Long genreId, String genreName) {
		this.genreId = genreId;
		this.genreName = genreName;
	}

	public Long getGenreId() {
		return genreId;
	}

	public String getGenreName() {
		return genreName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return genreId.equals(((CustomerGenre) obj).genreId);
	}

	@Override
	public int hashCode() {
		return genreId.hashCode();
	}

	@Override
	public String toString() {
		return genreName;
	}

}
