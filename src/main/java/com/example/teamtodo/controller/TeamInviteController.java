package com.example.teamtodo.controller;

import com.example.teamtodo.dto.TeamInviteRequest;
import com.example.teamtodo.dto.TeamInviteResponse;
import com.example.teamtodo.dto.TeamInviteStatusUpdateRequest;
import com.example.teamtodo.security.CustomUserDetails;
import com.example.teamtodo.service.TeamInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team-invites")
@RequiredArgsConstructor
public class TeamInviteController {

    private final TeamInviteService teamInviteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> inviteToTeam(
            @RequestBody @Valid TeamInviteRequest request,
            @AuthenticationPrincipal(expression = "userId") Long inviterId) {

        TeamInviteResponse response = teamInviteService.inviteToTeam(request, inviterId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "팀 초대가 성공적으로 발송되었습니다."
        ));
    }

    @PatchMapping("/{inviteId}")
    public TeamInviteResponse respondToInvite(
            @PathVariable Long inviteId,
            @RequestBody TeamInviteStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return teamInviteService.respondToInvite(inviteId, request, userDetails.getUserId());
    }

    @PutMapping("/{inviteId}/status")
    public ResponseEntity<Map<String, Object>> respondToInvite(
            @PathVariable Long inviteId,
            @RequestBody @Valid TeamInviteStatusUpdateRequest statusUpdateRequest,
            @AuthenticationPrincipal(expression = "userId") Long inviteeId) {

        TeamInviteResponse response = teamInviteService.respondToInvite(inviteId, statusUpdateRequest, inviteeId);

        String msg = response.getStatus().equals("ACCEPTED") ? "초대를 수락했습니다." : "초대를 거절했습니다.";

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", msg
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyInvites(
            @AuthenticationPrincipal(expression = "userId") Long inviteeId) {

        List<TeamInviteResponse> responses = teamInviteService.getInvitesForUser(inviteeId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", responses,
                "message", "초대 목록 조회 성공"
        ));
    }
}
