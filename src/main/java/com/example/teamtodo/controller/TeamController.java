package com.example.teamtodo.controller;

import com.example.teamtodo.domain.User;
import com.example.teamtodo.dto.*;
import com.example.teamtodo.exception.NotFoundException;
import com.example.teamtodo.repository.UserRepository;
import com.example.teamtodo.security.CustomUserDetails;
import com.example.teamtodo.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse createTeam(@Valid @RequestBody TeamCreateRequest request,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        // 이메일 → userId 조회
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        return teamService.createTeam(request, user.getUserId());
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyTeams(
            @AuthenticationPrincipal(expression = "userId") Long userId) {

        List<TeamResponse> myTeams = teamService.getMyTeams(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", myTeams,
                "message", "내가 속한 팀 목록 조회 성공"
        ));
    }

    @PutMapping("/{teamId}/delegate")
    public ResponseEntity<Map<String, Object>> delegateTeamLeader(
            @PathVariable Long teamId,
            @RequestBody @Valid TeamLeaderDelegateRequest request,
            @AuthenticationPrincipal(expression = "userId") Long currentLeaderId
    ) {
        teamService.delegateTeamLeader(teamId, currentLeaderId, request.getNewLeaderId());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "팀장 권한이 성공적으로 위임되었습니다."
        ));
    }

    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<Map<String, Object>> leaveTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        teamService.leaveTeam(teamId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "팀 탈퇴가 성공적으로 처리되었습니다."
        ));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<Map<String, Object>> getTeamMembers(@PathVariable Long teamId) {
        List<UserTeamResponse> members = teamService.getTeamMembers(teamId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", members,
                "message", "팀 멤버 목록 조회 성공"
        ));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Map<String, Object>> deleteTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getUserId();

        teamService.deleteTeam(teamId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "팀이 성공적으로 삭제되었습니다."
        ));
    }


    @GetMapping("/{teamId}/detail")
    public ResponseEntity<Map<String, Object>> getTeamDetail(@PathVariable Long teamId) {
        TeamDetailResponse response = teamService.getTeamDetail(teamId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "팀 상세 조회 성공"
        ));
    }

}
