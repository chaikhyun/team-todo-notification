package com.example.teamtodo.service;

import com.example.teamtodo.domain.Team;
import com.example.teamtodo.domain.User;
import com.example.teamtodo.dto.TeamCreateRequest;
import com.example.teamtodo.dto.TeamResponse;
import com.example.teamtodo.exception.NotFoundException;
import com.example.teamtodo.repository.TeamRepository;
import com.example.teamtodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamService userTeamService;


    public TeamResponse createTeam(TeamCreateRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

        Team team = Team.builder()
                .name(request.getName())
                .build();

        Team savedTeam = teamRepository.save(team);

        // user_team에 OWNER로 등록하는 로직 필요 (UserTeam 엔티티, 레포지토리 활용)
        // 예를 들면: userTeamRepository.save(new UserTeam(creator, savedTeam, "OWNER"));

        // 임시로 아래 주석 처리, 추후 userTeamService에서 처리 예정
        userTeamService.addMemberToTeam(creator, savedTeam, "OWNER");

        return TeamResponse.builder()
                .teamId(savedTeam.getTeamId())
                .name(savedTeam.getName())
                .build();
    }
}
