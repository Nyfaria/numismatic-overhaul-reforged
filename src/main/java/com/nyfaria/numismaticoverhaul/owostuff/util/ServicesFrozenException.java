package com.nyfaria.numismaticoverhaul.owostuff.util;

public class ServicesFrozenException extends IllegalStateException {
    public ServicesFrozenException(String message) {
        super(message);
    }
}
