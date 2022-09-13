package com.pdgc.avails.structures.criteria;

import java.io.Serializable;
import java.util.Objects;

public class SecondaryRightRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private RightRequest rightRequest;
    private TimeSpan windowLength; 
    private TimeSpan gapLength;
    
    public SecondaryRightRequest(
        RightRequest rightRequest,
        TimeSpan windowLength,
        TimeSpan gapLength
    ) {
        this.rightRequest = rightRequest;
        this.windowLength = windowLength;
        this.gapLength = gapLength;
    }
    
    public RightRequest getRightRequest() {
        return rightRequest;
    }
    
    public TimeSpan getWindowLength() {
        return windowLength;
    }
    
    public TimeSpan getGapLength() {
        return gapLength;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        
        return Objects.equals(rightRequest, ((SecondaryRightRequest)obj).rightRequest) 
            && Objects.equals(windowLength, ((SecondaryRightRequest)obj).windowLength) 
            && Objects.equals(gapLength, ((SecondaryRightRequest)obj).gapLength)
        ;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(rightRequest) 
            ^ Objects.hashCode(windowLength)
            ^ Objects.hashCode(gapLength)
        ;
    }

    @Override
    public String toString() {
        return "{RightRequest:" + rightRequest + ", WindowLength:" + windowLength + ", GapLength:" + gapLength + "}";
    }
}
