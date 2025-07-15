package com.example.teamtodo.dto;

import lombok.Getter;

@Getter
public class UserResponse {
    // getters
    private Long userId;
    private String username;
    private String email;

    public UserResponse(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

}
