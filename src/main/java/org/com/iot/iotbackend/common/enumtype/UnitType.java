package org.com.iot.iotbackend.common.enumtype;

public enum UnitType {
    TEMPERATURE("°C"),
    HUMIDITY("%"),
    MOISTURE("%");

    private final String value;

    UnitType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
