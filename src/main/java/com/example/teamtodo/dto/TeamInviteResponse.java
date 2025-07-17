package com.example.teamtodo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamInviteResponse {
    private Long inviteId;
    private Long teamId;
    private String teamName;
    private Long inviterId;
    private String inviterUsername;
    private Long inviteeId;
    private String inviteeUsername;
    private String status;
    private String createdAt;
    private String updatedAt;
}
