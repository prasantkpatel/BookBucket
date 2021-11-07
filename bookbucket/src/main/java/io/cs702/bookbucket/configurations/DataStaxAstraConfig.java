package io.cs702.bookbucket.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datastax.astra")
public class DataStaxAstraConfig {
    private String secureConnectBundle;

    public String getSecureConnectBundle() {
        return secureConnectBundle;
    }

    public void setSecureConnectBundle(String secureConnectBundle) {
        this.secureConnectBundle = secureConnectBundle;
    }

}
