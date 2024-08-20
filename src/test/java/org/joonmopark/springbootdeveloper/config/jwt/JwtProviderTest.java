package org.joonmopark.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;


    @DisplayName("generateToken: 유저정보와 만료기한을 기반으로 토큰을 만들수 있다.")
    @Test
    void generateToken(){
        //given
        final String email = "aaa@aaa.com";
        final String password = "password";
        final String nickname = "user1";
        final Duration expiredAt = Duration.ofSeconds(5);

        //when
        User testUser = userRepository.save(new User(email, password,nickname,  "ROLE_USER"));
        String token = jwtProvider.generateToken(testUser, expiredAt);

        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validateToken: 만료된 토큰일때 유효성 검증에 실패한다.")
    @Test
    void validateToken(){
        final Date now = new Date();

        String token = JwtFactory.builder()
                .expiration(new Date(now.getTime() - 5000))
                .build()
                .createToken(jwtProperties);

        assertThat(jwtProvider.validateToken(token)).isFalse();

    }

    @DisplayName("getAuthentication: 토큰으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication(){
        final String email = "test@email.com";
        final String token = JwtFactory
                .builder()
                .subject(email)
                .build()
                .createToken(jwtProperties);

        Authentication authentication = jwtProvider.getAuthentication(token);

        assertThat(((UserDetails)authentication.getPrincipal()).getUsername()).isEqualTo(email);
    }

    @DisplayName("getUserIdByToken: 토큰으로 유저 아이디를 구할 수 있다.")
    @Test
    void getUserIdByToken(){
        final Long userId = 1L;

        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        assertThat(jwtProvider.getUserId(token)).isEqualTo(userId);
    }
}
