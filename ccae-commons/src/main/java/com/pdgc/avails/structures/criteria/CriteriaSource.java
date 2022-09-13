package com.pdgc.avails.structures.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Territory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CriteriaSource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Object key;
    
    private Set<Media> medias;
    private Set<Territory> territories;
    private Set<Language> languages;
    
    private Set<OptionalWrapper<RightRequest>> primaryRequests;
    private Set<OptionalWrapper<SecondaryRightRequest>> secondaryPreRequests;
    private Set<OptionalWrapper<SecondaryRightRequest>> secondaryPostRequests;
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return Objects.equals(key, ((CriteriaSource) obj).key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
