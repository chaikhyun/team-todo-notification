package com.example.teamtodo.service;

import com.example.teamtodo.domain.Team;
import com.example.teamtodo.domain.User;
import com.example.teamtodo.domain.UserTeam;
import com.example.teamtodo.exception.DuplicateResourceException;
import com.example.teamtodo.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTeamService {

    private final UserTeamRepository userTeamRepository;

    public void addMemberToTeam(User user, Team team, String role) {
        userTeamRepository.findByUserAndTeam(user, team)
                .ifPresent(ut -> {
                    throw new DuplicateResourceException("이미 팀 멤버입니다.");
                });

        UserTeam userTeam = UserTeam.builder()
                .user(user)
                .team(team)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();

        userTeamRepository.save(userTeam);
    }
}
