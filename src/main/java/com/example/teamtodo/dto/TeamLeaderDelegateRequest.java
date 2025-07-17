package com.example.teamtodo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TeamLeaderDelegateRequest {
    @NotNull
    private Long newLeaderId;
}
