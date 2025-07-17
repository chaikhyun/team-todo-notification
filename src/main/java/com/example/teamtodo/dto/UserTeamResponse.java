package com.example.teamtodo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserTeamResponse {
    private Long userId;
    private String username;
    private String role;
    private LocalDateTime joinedAt;
}