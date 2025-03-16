package org.com.iot.iotbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/api/sensor")
public class SensorDataController {
    Logger logger = Logger.getLogger(SensorDataController.class.getName());

    @GetMapping("/data")
    public void getSensorData(String data) {
        logger.info(data);
    }
}
