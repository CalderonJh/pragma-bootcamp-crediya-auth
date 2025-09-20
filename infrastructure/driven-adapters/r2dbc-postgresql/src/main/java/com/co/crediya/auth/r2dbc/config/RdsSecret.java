package com.co.crediya.auth.r2dbc.config;

public record RdsSecret(
    String username,
    String password,
    String engine,
    String host,
    int port,
    String dbInstanceIdentifier) {}
