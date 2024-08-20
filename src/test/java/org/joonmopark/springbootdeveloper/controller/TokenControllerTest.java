package org.joonmopark.springbootdeveloper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.joonmopark.springbootdeveloper.config.jwt.JwtFactory;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProperties;
import org.joonmopark.springbootdeveloper.config.jwt.JwtProvider;
import org.joonmopark.springbootdeveloper.domain.RefreshToken;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.dto.AccessTokenRequest;
import org.joonmopark.springbootdeveloper.dto.AccessTokenResponse;
import org.joonmopark.springbootdeveloper.repository.TokenRepository;
import org.joonmopark.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.print.attribute.standard.Media;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    public void setMockMvc(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("createAccessToken: 리프레시토큰으로 새 액세스 토큰을 생성한다.")
    @Test
    public void createAccessToken() throws Exception {
        final String url = "/api/token";
        final String email = "test@test.com";
        final String password = "password";
        User user = userRepository.save(User.builder().email(email).password(password).build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", user.getId()))
                .build()
                .createToken(jwtProperties);

        tokenRepository.save(new RefreshToken(user.getId(), refreshToken));
        AccessTokenRequest request = new AccessTokenRequest(refreshToken);

        ResultActions result = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

    }
}
