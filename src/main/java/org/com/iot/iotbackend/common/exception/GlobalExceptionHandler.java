package org.com.iot.iotbackend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.com.iot.iotbackend.dto.common.SingleDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<SingleDataResponse> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        MetaData metaData = MetaData.ofError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        log.error(ex.getMessage(), ex);

        SingleDataResponse response = new SingleDataResponse(metaData);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Valid 유효성 Exception 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SingleDataResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // DTO에서 설정한 message만 가져오기
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage()) // DTO에 설정된 message 가져오기
                .collect(Collectors.toList());

        // 첫 번째 오류 메시지만 응답 (여러 개를 리스트로 반환하고 싶으면 errorMessages 그대로 사용)
        String errorMessage = errorMessages.isEmpty() ? "Invalid request" : errorMessages.get(0);

        // 응답 생성
        MetaData metaData = MetaData.ofError(HttpStatus.BAD_REQUEST.value(), errorMessage);
        SingleDataResponse<?> response = new SingleDataResponse<>(metaData);

        return ResponseEntity.badRequest().body(response);
    }

    // 그 외 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SingleDataResponse> handleGenericException(HttpServletRequest request, Exception ex) {
        MetaData metaData = MetaData.ofError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error: "+ ex.getMessage());
        SingleDataResponse response = new SingleDataResponse(metaData);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
