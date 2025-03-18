package org.com.iot.iotbackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.iot.iotbackend.dto.auth.SignupRequest;
import org.com.iot.iotbackend.entity.User;
import org.com.iot.iotbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    // 비밀키
    @Value("${jwt.email-verify}")
    private String JWT_SECRET_KEY;

    private final UserRepository userRepository;

    @Transactional
    public void signup(SignupRequest request) {
        signupValidation(request);

        // 패스워드 암호화
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity();
        user.setEncodedPassword(encodedPassword);
        userRepository.save(user);
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

    // 본인인증 메일 관련 메소드
    public String generateVerifyCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }

    public String generateVerifyAccessToken(String email, String verifyCode) {
        // 만료 시간 설정
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 5 * 60 * 1000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("email", email)
                .claim("verifyCode", verifyCode)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()))
                .compact();
    }

    public void validateVerifyAccessToken(String jwtToken, String verifyCode) {
        if(jwtToken == null) {
            throw new RuntimeException("본인인증 시도 후 인증코드를 입력해주세요.");
        }

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        try {
            // JWT 토큰 파싱
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            // JWT 토큰 검증
            String payloadEmail = claims.get("email", String.class);
            String payloadVerifyCode = claims.get("verifyCode", String.class);

            // 발급된 인증코드와 메일 인증코드 일치 검증
            if (!verifyCode.equals(payloadVerifyCode)) {
                throw new RuntimeException("Invalid verification code");
            }
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Expired JWT token");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
