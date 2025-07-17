package com.example.teamtodo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamResponse {
    private Long teamId;
    private String name;
    private LocalDateTime createdAt;
}
