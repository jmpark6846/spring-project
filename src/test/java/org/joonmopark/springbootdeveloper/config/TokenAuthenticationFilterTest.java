package org.joonmopark.springbootdeveloper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProvider;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Duration;

@SpringBootTest
public class TokenAuthenticationFilterTest {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    TokenAuthenticationFilter tokenAuthenticationFilter;
    MockFilterChain mockFilterChain;
    MockHttpServletRequest req;
    MockHttpServletResponse res;
    User user;


    @BeforeEach
    void setup(){
        userRepository.deleteAll();

        tokenAuthenticationFilter = new TokenAuthenticationFilter(jwtProvider);
        mockFilterChain = new MockFilterChain();
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();

        user = userRepository.save(User.builder()
                .email("test@gmail.com")
                .password("password")
                .nickname("user1").build());
    }

    @DisplayName("authenticateRequest: 헤더에 엑세스 토큰이 있는 경우 인증 정보를 SecurityContextHolder에 저장한다.")
    @Test
    public void authenticateRequest() throws ServletException, IOException {
        String accessToken = jwtProvider.generateToken(user, Duration.ofDays(1));
        String tokenHeaderValue = TokenAuthenticationFilter.TOKEN_PREFIX + accessToken;
        req.addHeader(TokenAuthenticationFilter.HEADER_AUTHORIZATION, tokenHeaderValue);

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

        tokenAuthenticationFilter.doFilter(req, res, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Assertions.assertNotNull(authentication);
        Assertions.assertEquals(((UserDetails) authentication.getPrincipal()).getUsername(), user.getUsername());
    }
}
