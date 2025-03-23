package org.com.iot.iotbackend.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.iot.iotbackend.dto.auth.request.LoginRequest;
import org.com.iot.iotbackend.dto.auth.response.LoginResponse;
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

    @Value("${app.env}")
    private String APP_ENV;

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
            return false;
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

    // 리프레쉬 토큰, 액세스 토큰 재발급 관련 로직
    // 토큰이 만료되었는지 확인하는 메서드
    public boolean isTokenExpired(String token) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return false; // 토큰이 유효하면 만료되지 않음
        } catch (ExpiredJwtException e) {
            return true; // 토큰이 만료됨
        } catch (Exception e) {
            return false; // 다른 예외는 만료가 아닌 다른 이유로 실패
        }
    }

    // 리프레쉬 토큰 검증 메서드
    public boolean validateRefreshToken(String refreshToken) {
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("리프레쉬 토큰이 만료되었습니다.");
        } catch (Exception e) {
            System.out.println("유효하지 않은 리프레쉬 토큰입니다.");
        }
        return false;
    }

    // 리프레쉬 토큰을 사용하여 새로운 액세스 토큰 발급
    public String reissueAccessToken(String refreshToken) {
        if (!validateRefreshToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레쉬 토큰입니다.");
        }

        // 리프레쉬 토큰에서 이메일 추출
        String email = getEmailFromToken(refreshToken);

        // 새로운 액세스 토큰 생성
        Date now = new Date();
        Date accessTokenExpirationDate = new Date(now.getTime() + 15 * 60 * 1000); // 액세스 토큰 유효기간 : 15분

        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        String newAccessToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return newAccessToken;
    }

    // 요청에서 액세스 토큰 추출 (필터에서 사용)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 요청에서 리프레쉬 토큰 추출 (필터에서 사용)
    public String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken != null) {
            return refreshToken;
        }
        return null;
    }

    public void setHttpOnlyCookie(LoginResponse data, HttpServletResponse httpResponse) {
        // 환경에 따라 Secure 설정, 배포(https) : true, 로컬(http) : false
        boolean isProd = APP_ENV.equals("docker");
        // 환경에 따라 SameSite 설정 배포(https) : None, 로컬(http) : SameSite None 설정 시, Secure(true) 환경에서만 가능
        String sameSiteConfig = APP_ENV.equals("docker") ? "None" : "Strict";

        Cookie accessTokenCookie = new Cookie("Authorization", data.getJwtTokens().getAccessToken());
        accessTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        accessTokenCookie.setSecure(isProd);  // Secure 설정
        accessTokenCookie.setPath("/");    // 모든 경로에서 유효
        accessTokenCookie.setMaxAge(15 * 60); // 유효 기간: 15분 (Access 토큰의 유효 기간과 동일)
        accessTokenCookie.setAttribute("SameSite", sameSiteConfig);
        httpResponse.addCookie(accessTokenCookie);

        // Refresh 토큰을 HTTP-Only 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("Refresh-Token", data.getJwtTokens().getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // HTTP-Only 설정: JavaScript에서 접근 불가
        refreshTokenCookie.setSecure(isProd);  // Secure 설정
        refreshTokenCookie.setPath("/");    // 모든 경로에서 유효
        refreshTokenCookie.setMaxAge(24 * 60 * 60); // 유효 기간: 1일 (Refresh 토큰의 유효 기간과 동일)
        refreshTokenCookie.setAttribute("SameSite", sameSiteConfig);
        httpResponse.addCookie(refreshTokenCookie);
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
