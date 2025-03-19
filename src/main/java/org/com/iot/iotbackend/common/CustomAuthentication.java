package org.com.iot.iotbackend.common;

public class CustomAuthentication {
    private final String email;

    public CustomAuthentication(String email) {
        this.email = email;
    }

    public String getName() {
        return email;
    }
}

