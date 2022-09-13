package com.pdgc.avails.structures.criteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * Class to describe the minimum window length parameter of the avails criteria
 * 
 * @author Vishal Raut
 */
public class TimeSpan implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private EnumMap<TimeUnit, Integer> windowComponents;

    public TimeSpan() {
        windowComponents = new EnumMap<>(TimeUnit.class);
    }
    
	public TimeSpan(Collection<TimeSpanComponent> unsortedComponents) {
	    windowComponents = new EnumMap<>(TimeUnit.class);
	    for (TimeSpanComponent component : unsortedComponents) {
	        if (component.getLength() != 0) {
	            windowComponents.merge(component.getTimeUnit(), component.getLength(), Integer::sum);
	        }
		}
	}

    public Integer getLengthForUnit(TimeUnit unit) {
        return windowComponents.getOrDefault(unit, 0);
    }
    
    /**
     * Returns true if this timeSpan doesn't actually represent anything b/c it has no non-zero components
     * @return
     */
    public boolean isEmpty() {
        return windowComponents.isEmpty();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        
        return Objects.equals(windowComponents, ((TimeSpan)obj).windowComponents);
    }
    
    @Override
    public int hashCode() {
        return windowComponents.hashCode();
    }
    
    @Override
    public String toString() {
        Collection<String> componentStrings = new ArrayList<>();
        for (Entry<TimeUnit, Integer> entry : windowComponents.entrySet()) {
            componentStrings.add(TimeSpanComponent.getString(entry.getValue(), entry.getKey()));
        }
        return StringUtils.join(componentStrings, " ");
    }
}