package org.com.iot.iotbackend.dto.sensor;

import lombok.Data;

@Data
public class SensorWarningResponse {
    private int dhtMinWarningCount;
    private int dhtMaxWarningCount;
    private int soilMinWarningCount;
    private int soilMaxWarningCount;

    public SensorWarningResponse() {
        this.dhtMinWarningCount = 0;
        this.dhtMaxWarningCount = 0;
        this.soilMinWarningCount = 0;
        this.soilMaxWarningCount = 0;
    }
}
