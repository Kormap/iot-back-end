package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    Logger logger = Logger.getLogger(AuthController.class.getName());

    @Operation(summary = "테스트 단건 조회", description = "테스트를 단건 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "example read : SUCCESS"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<String> testByGet(
            @Schema(example = "abc1234")
            @PathVariable(required = true) String id
    ) {
        logger.info("API :: testByGet called");
        return ResponseEntity.ok("test id parameter : ".concat(id));
    }

}
