package com.pdgc.avails.structures.criteria;

public class TimeSpanComponent {

    private int length;
    private TimeUnit timeUnit = TimeUnit.DAYS;

    public TimeSpanComponent(int length, TimeUnit timeUnit) {
        this.length = length;
        this.timeUnit = timeUnit;
    }
    
    public int getLength() {
        return length;
    }
    
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public String toString() {
        return getString(length, timeUnit);
    }
    
    public static String getString(int length, TimeUnit timeUnit) {
        return length + " " + (timeUnit == null ? "? units" : timeUnit.getDescription() + ((length > 1) ? "s" : ""));
    }
}
