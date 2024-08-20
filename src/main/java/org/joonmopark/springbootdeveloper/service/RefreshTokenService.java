package org.joonmopark.springbootdeveloper.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProvider;
import org.joonmopark.springbootdeveloper.domain.RefreshToken;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.repository.TokenRepository;
import org.joonmopark.springbootdeveloper.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service

@RequiredArgsConstructor
public class RefreshTokenService {

    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    public RefreshToken findOrCreateByUserId(Long userId){
        try{
            RefreshToken refreshToken = tokenRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("no refreshToken"));
            if(!jwtProvider.validateToken(refreshToken.getRefreshToken())){
                throw new IllegalArgumentException("refresh token validation failed");
            }
            return refreshToken;
        }catch(Exception e){
            User user = userService.findByUserId(userId);
            String refreshToken = jwtProvider.generateToken(user, Duration.ofDays(1));
            RefreshToken newRefreshToken = tokenRepository.save(new RefreshToken(userId, refreshToken));
            return newRefreshToken;
        }
    }
    public RefreshToken findByRefreshToken(String refreshToken){
        return tokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->new IllegalArgumentException("존재하지 않는 refresh token 입니다."));
    }

    /*
    * 로그인시도 UserController
    * 리프레시 토큰 있는지 확인 UserController
    * -> 리프레시 토큰 검증 -> 엑세스 토큰 생성 or 레프레시 토큰, 엑세스토큰 생성
    * */
}
