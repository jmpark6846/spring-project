package org.joonmopark.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProvider;
import org.joonmopark.springbootdeveloper.domain.RefreshToken;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.repository.TokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createAccessToken(String refreshToken){
        if(!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("malformed token");
        }else{
            Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
            User user = userService.findByUserId(userId);
            String accessToken = jwtProvider.generateToken(user,Duration.ofDays(1));
            return accessToken;
        }
    }
}
