package org.joonmopark.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.joonmopark.springbootdeveloper.dto.AccessTokenRequest;
import org.joonmopark.springbootdeveloper.dto.AccessTokenResponse;
import org.joonmopark.springbootdeveloper.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<AccessTokenResponse> createAccessToken(@RequestBody AccessTokenRequest request){
        String accessToken = tokenService.createAccessToken(request.getRefreshToken());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AccessTokenResponse(accessToken));
    }
}
