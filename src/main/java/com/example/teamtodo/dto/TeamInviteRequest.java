package com.example.teamtodo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamInviteRequest {
    @NotNull
    private Long teamId;

    @NotNull
    @Email
    private String inviteeEmail;
}
