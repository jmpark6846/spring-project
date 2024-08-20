package org.joonmopark.springbootdeveloper.dto;

import org.joonmopark.springbootdeveloper.domain.User;

public class UserResponse {
    String email;

    public UserResponse(User user) {
        this.email = user.getEmail();
    }
}
