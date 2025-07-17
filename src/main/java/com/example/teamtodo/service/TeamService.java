package com.example.teamtodo.service;

import com.example.teamtodo.domain.Team;
import com.example.teamtodo.domain.Todo;
import com.example.teamtodo.domain.User;
import com.example.teamtodo.domain.UserTeam;
import com.example.teamtodo.dto.TeamCreateRequest;
import com.example.teamtodo.dto.TeamDetailResponse;
import com.example.teamtodo.dto.TeamResponse;
import com.example.teamtodo.dto.UserTeamResponse;
import com.example.teamtodo.exception.NotFoundException;
import com.example.teamtodo.repository.TeamRepository;
import com.example.teamtodo.repository.TodoRepository;
import com.example.teamtodo.repository.UserRepository;
import com.example.teamtodo.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserRepository userRepository;
    private final UserTeamService userTeamService;
    private final TodoRepository todoRepository;

    public TeamResponse createTeam(TeamCreateRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

        Team team = Team.builder()
                .name(request.getName())
                .build();

        Team savedTeam = teamRepository.save(team);

        // user_team에 OWNER로 등록하는 로직 필요 (UserTeam 엔티티, 레포지토리 활용)
        // 예를 들면: userTeamRepository.save(new UserTeam(creator, savedTeam, "LEADER"));

        // 임시로 아래 주석 처리, 추후 userTeamService에서 처리 예정
        userTeamService.addMemberToTeam(creator, savedTeam, "LEADER");

        return TeamResponse.builder()
                .teamId(savedTeam.getTeamId())
                .name(savedTeam.getName())
                .build();
    }

    public List<TeamResponse> getMyTeams(Long userId) {
        List<UserTeam> userTeams = userTeamRepository.findAllByUser_UserId(userId);

        return userTeams.stream()
                .map(userTeam -> {
                    Team team = userTeam.getTeam();
                    return TeamResponse.builder()
                            .teamId(team.getTeamId())
                            .name(team.getName())
                            .createdAt(team.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void delegateTeamLeader(Long teamId, Long currentLeaderId, Long newLeaderId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("팀이 존재하지 않습니다."));

        // 현재 리더 확인
        User currentLeader = userRepository.findById(currentLeaderId)
                .orElseThrow(() -> new NotFoundException("현재 사용자 정보가 없습니다."));

        UserTeam leaderRelation = userTeamRepository.findByUserAndTeam(currentLeader, team)
                .orElseThrow(() -> new NotFoundException("팀 멤버가 아닙니다."));

        if (!"LEADER".equals(leaderRelation.getRole())) {
            throw new IllegalArgumentException("팀장만 위임할 수 있습니다.");
        }
        // 위임 대상 확인
        User newLeader = userRepository.findById(newLeaderId)
                .orElseThrow(() -> new NotFoundException("위임 대상 사용자가 존재하지 않습니다."));

        UserTeam newLeaderRelation = userTeamRepository.findByUserAndTeam(newLeader, team)
                .orElseThrow(() -> new IllegalArgumentException("위임 대상은 팀에 속한 사용자여야 합니다."));

        // 역할 변경
        leaderRelation.setRole("MEMBER");
        newLeaderRelation.setRole("LEADER");

        userTeamRepository.save(leaderRelation);
        userTeamRepository.save(newLeaderRelation);
    }

    @Transactional
    public void leaveTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("팀이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자가 존재하지 않습니다."));

        UserTeam userTeam = userTeamRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버가 아닙니다."));

        // 팀장이면 탈퇴 불가 (필요 시 정책 변경)
        if ("LEADER".equals(userTeam.getRole())) {
            throw new IllegalStateException("팀장은 탈퇴할 수 없습니다. 팀장 위임 후 탈퇴하세요.");
        }

        userTeamRepository.delete(userTeam);
    }


    @Transactional(readOnly = true)
    public List<UserTeamResponse> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("팀이 존재하지 않습니다."));

        List<UserTeam> members = userTeamRepository.findAllByTeam(team);

        return members.stream()
                .map(ut -> UserTeamResponse.builder()
                        .userId(ut.getUser().getUserId())
                        .username(ut.getUser().getUsername())
                        .role(ut.getRole())
                        .joinedAt(ut.getJoinedAt())
                        .build())
                .collect(Collectors.toList());
    }


    public void deleteTeam(Long teamId, Long requesterId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

        // 팀장인지 확인 (예: UserTeam에서 role == 'LEADER'인지 확인)
        boolean isLeader = team.getUserTeams().stream()
                .anyMatch(ut -> ut.getUser().getUserId().equals(requesterId) && "LEADER".equals(ut.getRole()));

        if (!isLeader) {
            throw new IllegalStateException("팀장만 팀을 삭제할 수 있습니다.");
        }

        teamRepository.delete(team);
    }

    public TeamDetailResponse getTeamDetail(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

        List<UserTeam> userTeams = userTeamRepository.findByTeam(team);
        List<Todo> todos = todoRepository.findByTeam(team); // 추후 구현된다고 가정

        User leader = userTeams.stream()
                .filter(ut -> ut.getRole().equals("LEADER"))
                .map(UserTeam::getUser)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("팀장을 찾을 수 없습니다."));

        List<TeamDetailResponse.MemberInfo> memberInfos = userTeams.stream()
                .map(ut -> TeamDetailResponse.MemberInfo.builder()
                        .userId(ut.getUser().getUserId())
                        .username(ut.getUser().getUsername())
                        .role(ut.getRole())
                        .build())
                .collect(Collectors.toList());

        List<TeamDetailResponse.TodoInfo> todoInfos = todos.stream()
                .map(todo -> TeamDetailResponse.TodoInfo.builder()
                        .assigneeId(todo.getAssignee().getUserId())
                        .assigneeName(todo.getAssignee().getUsername())
                        .title(todo.getTitle())
                        .status(todo.getStatus())
                        .dueDate(todo.getDueDate().toString())
                        .build())
                .collect(Collectors.toList());

        return TeamDetailResponse.builder()
                .teamId(team.getTeamId())
                .teamName(team.getName())
                .leader(TeamDetailResponse.LeaderInfo.builder()
                        .userId(leader.getUserId())
                        .username(leader.getUsername())
                        .email(leader.getEmail())
                        .build())
                .members(memberInfos)
                .memberCount(memberInfos.size())
                .todos(todoInfos)
                .build();
    }

}
