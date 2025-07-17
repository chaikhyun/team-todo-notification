package com.example.teamtodo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamResponse {
    private Long teamId;
    private String name;
}
