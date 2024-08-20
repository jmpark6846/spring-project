package org.joonmopark.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProperties;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProvider;
import org.joonmopark.springbootdeveloper.domain.RefreshToken;
import org.joonmopark.springbootdeveloper.repository.TokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    public final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String TOKEN_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    // TokenAuthenticationFilter의 역할: 매 리퀘스트마다 doFilterInternal 실행 -> 엑세스토큰이 있으면, 토큰으로 유저 조회 후 유저 정보를 시큐리티 컨텍스트 홀더에 저장
    //                                  즉 액세스 토큰을 유저로 바꿔 로그인한 상태로 만들어줌.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String accessToken = getAccessToken(authorizationHeader);

        if (jwtProvider.validateToken(accessToken)){
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader)  {
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
