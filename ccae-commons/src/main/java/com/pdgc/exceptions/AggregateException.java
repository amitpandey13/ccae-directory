package com.pdgc.exceptions;

public class AggregateException extends Exception {

    private static final long serialVersionUID = 1L;

    public AggregateException(String message, Iterable<? extends Throwable> children) {
        super(message);
        for (Throwable t : children) {
            addSuppressed(t);
        }
    }
}
