package com.pdgc.general.structures.rightsource;

import java.io.Serializable;
import java.util.Objects;

public class FoxDroType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String workbooklabel;

    public FoxDroType(Long id, String name, String workbooklabel) {
        this.id = id;
        this.name = name;
        this.workbooklabel = workbooklabel;
    }

    public FoxDroType() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWorkbooklabel() {
        return workbooklabel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(id, ((FoxDroType) obj).id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name;
    }

}
