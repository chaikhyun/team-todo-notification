package com.example.teamtodo.service;

import com.example.teamtodo.domain.Team;
import com.example.teamtodo.domain.TeamInvite;
import com.example.teamtodo.domain.User;
import com.example.teamtodo.dto.TeamInviteRequest;
import com.example.teamtodo.dto.TeamInviteResponse;
import com.example.teamtodo.dto.TeamInviteStatusUpdateRequest;
import com.example.teamtodo.exception.NotFoundException;
import com.example.teamtodo.exception.DuplicateResourceException;
import com.example.teamtodo.repository.TeamInviteRepository;
import com.example.teamtodo.repository.TeamRepository;
import com.example.teamtodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamInviteService {

    private final TeamInviteRepository teamInviteRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamService userTeamService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TeamInviteResponse inviteToTeam(TeamInviteRequest request, Long inviterId) {

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new NotFoundException("초대자 정보를 찾을 수 없습니다."));

        User invitee = userRepository.findByEmail(request.getInviteeEmail())
                .orElseThrow(() -> new NotFoundException("초대 대상 사용자가 존재하지 않습니다."));

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundException("팀 정보를 찾을 수 없습니다."));

        // 중복 초대 체크
        teamInviteRepository.findByInvitee_UserIdAndTeam_TeamId(invitee.getUserId(), team.getTeamId())
                .ifPresent(invite -> {
                    throw new DuplicateResourceException("이미 초대가 존재합니다.");
                });

        TeamInvite invite = TeamInvite.builder()
                .inviter(inviter)
                .invitee(invitee)
                .team(team)
                .status(TeamInvite.Status.PENDING)
                .build();

        teamInviteRepository.save(invite);

        return toResponse(invite);
    }

    public TeamInviteResponse respondToInvite(Long inviteId, TeamInviteStatusUpdateRequest request, Long inviteeId) {
        TeamInvite invite = teamInviteRepository.findById(inviteId)
                .orElseThrow(() -> new NotFoundException("초대 정보를 찾을 수 없습니다."));

        if (!invite.getInvitee().getUserId().equals(inviteeId)) {
            throw new IllegalArgumentException("본인의 초대에만 응답할 수 있습니다.");
        }

        TeamInvite.Status newStatus;
        try {
            newStatus = TeamInvite.Status.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("상태 값이 올바르지 않습니다.");
        }

        invite.setStatus(newStatus);

        // 초대 수락시 user_team에 멤버 추가
        if (newStatus == TeamInvite.Status.ACCEPTED) {
            Team team = invite.getTeam();
            User invitee = invite.getInvitee();
            userTeamService.addMemberToTeam(invitee, team, "MEMBER");  // role은 MEMBER 고정
        }
        teamInviteRepository.save(invite);

        return toResponse(invite);
    }

    public List<TeamInviteResponse> getInvitesForUser(Long inviteeId) {
        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

        List<TeamInvite> invites = teamInviteRepository.findByInvitee(invitee);

        return invites.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TeamInviteResponse toResponse(TeamInvite invite) {
        return TeamInviteResponse.builder()
                .inviteId(invite.getInviteId())
                .teamId(invite.getTeam().getTeamId())
                .teamName(invite.getTeam().getName())
                .inviterId(invite.getInviter().getUserId())
                .inviterUsername(invite.getInviter().getUsername())
                .inviteeId(invite.getInvitee().getUserId())
                .inviteeUsername(invite.getInvitee().getUsername())
                .status(invite.getStatus().name())
                .createdAt(invite.getCreatedAt().format(formatter))
                .updatedAt(invite.getUpdatedAt().format(formatter))
                .build();
    }
}
