package org.com.iot.iotbackend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("status", HttpStatus.BAD_REQUEST.value());
        metaData.put("message", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("metaData", metaData);

        log.error(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 그 외 모든 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(HttpServletRequest request, Exception ex) {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        metaData.put("message", "Internal Server Error: " + ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("metaData", metaData);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
