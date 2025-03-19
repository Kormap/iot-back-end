package org.com.iot.iotbackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.common.JwtTokenProvider;
import org.com.iot.iotbackend.common.JwtTokens;
import org.com.iot.iotbackend.dto.auth.request.LoginRequest;
import org.com.iot.iotbackend.dto.auth.request.SignupRequest;
import org.com.iot.iotbackend.dto.auth.response.LoginResponse;
import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        signupValidation(request);

        String encodedInputPassword = encryptPassword(request.getPassword());
        User user = request.toEntity();
        user.setEncodedPassword(encodedInputPassword);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("회원가입된 이메일주소가 아닙니다."));

        checkPassword(request.getPassword(), user.getPassword());

        // TODO 로그인 유효성 검사, JWT토큰 발급
        JwtTokens jwtTokens = jwtTokenProvider.generateAuthTokens(request);

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                jwtTokens
        );
    }

    // 회원가입 유효성 검사 관련 메소드
    public void signupValidation(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 회원가입된 이메일 주소입니다.");
        }
        // 비밀번호, 비밀번호 확인 비교
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다. 다시 입력해주세요.");
        }
    }

    public void checkPassword(String inputPassword, String encodedPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 비밀번호 암호화 관련 메소드
    public String encryptPassword(String inputPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(inputPassword);
    }

}
