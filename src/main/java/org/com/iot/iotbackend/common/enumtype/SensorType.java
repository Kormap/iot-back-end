package org.com.iot.iotbackend.common.enumtype;

public enum SensorType {
    DHT_TEMPERATURE("DHT_TEMPERATURE"),
    DHT_HUMIDITY("DHT_HUMIDITY"),
    SOIL_MOISTURE("SOIL_MOISTURE");

    private final String value;

    SensorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
