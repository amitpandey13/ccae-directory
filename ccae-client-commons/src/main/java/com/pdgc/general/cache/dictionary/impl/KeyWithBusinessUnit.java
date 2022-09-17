package com.pdgc.general.cache.dictionary.impl;

import java.io.Serializable;

/**
 * A number of data objects can have the same id's but different business units.  For example, media morph and tuscany each have customers with id = 10, but
 * each system has a different business unit.  The pk of the table is the id AND the business unit. Therefore the key of the object must be composite.
 *
 * @author atarshis
 */
public class KeyWithBusinessUnit<K> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K id;
    private Long businessUnitId;

    public KeyWithBusinessUnit(K id, Long businessUnitId) {
        super();
        this.id = id;
        this.businessUnitId = businessUnitId;
    }

    public K getId() {
        return id;
    }

    public Long getBusinessUnitId() {
        return businessUnitId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((businessUnitId == null) ? 0 : businessUnitId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KeyWithBusinessUnit<?> other = (KeyWithBusinessUnit<?>) obj;
        if (businessUnitId == null) {
            if (other.businessUnitId != null) {
                return false;
            }
        } else if (!businessUnitId.equals(other.businessUnitId)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "/" + businessUnitId;
    }

}
