package org.joonmopark.springbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenRequest {
    private String refreshToken;

    public AccessTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
