package com.example.teamtodo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamInviteStatusUpdateRequest {
    @NotNull
    private String status;  // ACCEPTED or REJECTED
}
