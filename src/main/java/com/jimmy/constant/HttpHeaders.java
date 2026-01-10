package com.jimmy.constant;

import lombok.Getter;

@Getter
public enum HttpHeaders {
    TRACKING_ID("TrackingId"),
    AUTHORIZATION("Authorization");

    private final String name;

    HttpHeaders(String name) {
        this.name = name;
    }
}
