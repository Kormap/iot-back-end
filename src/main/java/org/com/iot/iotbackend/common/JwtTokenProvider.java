package org.com.iot.iotbackend.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.com.iot.iotbackend.dto.auth.request.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

@Component
public class JwtTokenProvider {
    // 비밀키
    @Value("${jwt.secret-key}")
    private String JWT_SECRET_KEY;

    // 로그인용 토큰 생성
    public JwtTokens generateAuthTokens(LoginRequest request) {
        Date now = new Date();
        Date accessTokenExpirationDate = new Date(now.getTime() + 15 * 60 * 1000);      // 액세스 토큰 유효기간 : 15분
        Date refreshTokenExpirationDate = new Date(now.getTime() + 1 * 24 * 60 * 1000); // 리프레쉬 토큰 유효기간 : 1일

        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        String accessToken = Jwts.builder()
                .setSubject(request.getEmail())
                .setIssuedAt(now)
                .setExpiration(accessTokenExpirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(request.getEmail())
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return new JwtTokens(accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT 토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // JWT에서 이메일 추출
    public String getEmailFromToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // email이 subject로 저장된 경우
    }

    public CustomAuthentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        return new CustomAuthentication(email);
    }

    // 이메일 인증용 토큰 생성 관련
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

        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        String verifyAccessToken = Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .claim("verifyCode", verifyCode)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return "Bearer " + verifyAccessToken;
    }

    public void validateVerifyAccessToken(String jwtToken, String email, String verifyCode) {
        if (jwtToken == null) {
            throw new RuntimeException("본인인증 시도 후 인증코드를 입력해주세요.");
        }

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7).trim();
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        try {
            // JWT 토큰 파싱
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            // JWT 토큰 검증
            String payloadEmail = claims.get("email", String.class);
            String payloadVerifyCode = claims.get("verifyCode", String.class);

            // 발급된 인증코드와 메일 인증코드 일치 검증
            if (!payloadEmail.equals(email)) {
                throw new RuntimeException("전송된 이메일의 인증코드가 아닙니다.");
            }
            if (!verifyCode.equals(payloadVerifyCode)) {
                throw new RuntimeException("유효하지 않은 인증코드입니다.");
            }
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("인증 유효시간이 초과되었습니다. 새로운 인증 요청을 해주세요.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
