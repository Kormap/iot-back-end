package org.com.iot.iotbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.dto.common.CommonResponse;
import org.com.iot.iotbackend.dto.common.MetaData;
import org.com.iot.iotbackend.dto.user.request.UpdateProfileRequest;
import org.com.iot.iotbackend.dto.user.response.ProfileResponse;
import org.com.iot.iotbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 개인정보 조회 API", description = "유저의 개인정보 조회를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @GetMapping("/{id}/profile")
    public ResponseEntity<CommonResponse<ProfileResponse>> getUserProfile(@PathVariable Long id) {
        ProfileResponse data = userService.getUserProfile(id);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData, data);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "유저 개인정보 수정 API", description = "유저의 개인정보 수정을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PatchMapping("/me")
    public ResponseEntity<CommonResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        userService.updateProfile(request);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "유저 삭제 API", description = "유저의 계정 삭제를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Succeeded"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data.")
    })
    @PostMapping("/{id}/remove")
    public ResponseEntity<CommonResponse> removeUser(@PathVariable Long id) {
        userService.removeUser(id);

        MetaData metaData = MetaData.of("OK: Succeeded");
        CommonResponse response = new CommonResponse<>(metaData);
        return ResponseEntity.ok(response);
    }
}
