package com.example.teamtodo.repository;

import com.example.teamtodo.domain.UserTeam;
import com.example.teamtodo.domain.Team;
import com.example.teamtodo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    Optional<UserTeam> findByUserAndTeam(User user, Team team);
    List<UserTeam> findAllByUser(User user);
    List<UserTeam> findAllByUser_UserId(Long userId);
    List<UserTeam> findAllByTeam(Team team);
    List<UserTeam> findByTeam(Team team);

}
