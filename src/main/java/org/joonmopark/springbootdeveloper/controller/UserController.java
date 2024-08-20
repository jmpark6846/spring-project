package org.joonmopark.springbootdeveloper.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.dto.AddUserRequest;
import org.joonmopark.springbootdeveloper.dto.LoginRequest;
import org.joonmopark.springbootdeveloper.dto.LoginResponse;
import org.joonmopark.springbootdeveloper.dto.UserResponse;
import org.joonmopark.springbootdeveloper.service.RefreshTokenService;
import org.joonmopark.springbootdeveloper.service.TokenService;
import org.joonmopark.springbootdeveloper.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;


    @PostMapping("/api/user")
    public ResponseEntity<UserResponse> signup(@RequestBody AddUserRequest request){
        User user = userService.save(request);
        return ResponseEntity
                .status(201)
                .body(new UserResponse(user));
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        User user = userService.findByEmail(request.getEmail());
        String refreshToken = refreshTokenService.findOrCreateByUserId(user.getId()).getRefreshToken();
        String accessToken = tokenService.createAccessToken(refreshToken);
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    @GetMapping("/api/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok().build();
    }
}
