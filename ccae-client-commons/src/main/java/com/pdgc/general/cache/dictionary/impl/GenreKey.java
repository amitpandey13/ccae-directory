package com.pdgc.general.cache.dictionary.impl;

/**
 * Composite key for Genre
 */
public class GenreKey {

    private String id;
    private Long businessUnitId;
    private Long genreType;

    public GenreKey(String id, Long businessUnitId, Long genreType) {
        this.id = id;
        this.businessUnitId = businessUnitId;
        this.genreType = genreType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBusinessUnitId() {
        return this.businessUnitId;
    }

    public void setBusinessUnitId(Long businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public Long getGenreType() {
        return this.genreType;
    }

    public void setGenreType(Long genreType) {
        this.genreType = genreType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GenreKey)) {
            return false;
        }
        final GenreKey other = (GenreKey) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object id1 = this.getId();
        final Object id2 = other.getId();
        if (id1 == null ? id2 != null : !id1.equals(id2)) {
            return false;
        }
        final Object businessUnitId1 = this.getBusinessUnitId();
        final Object businessUnitId2 = other.getBusinessUnitId();
        if (businessUnitId1 == null ? businessUnitId2 != null : !businessUnitId1.equals(businessUnitId2)) {
            return false;
        }
        final Object genreType1 = this.getGenreType();
        final Object genreType2 = other.getGenreType();
        return !(genreType1 == null ? genreType2 != null : !genreType1.equals(genreType2));
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object id1 = this.getId();
        result = result * PRIME + (id1 == null ? 43 : id1.hashCode());
        final Object businessUnitId1 = this.getBusinessUnitId();
        result = result * PRIME + (businessUnitId1 == null ? 43 : businessUnitId1.hashCode());
        final Object genreType1 = this.getGenreType();
        result = result * PRIME + (genreType1 == null ? 43 : genreType1.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GenreKey;
    }

    public String toString() {
        return "GenreKey(id=" + this.getId() + ", businessUnitId=" + this.getBusinessUnitId() + ", genreType=" + this.getGenreType() + ")";
    }
}
