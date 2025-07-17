package com.example.teamtodo.repository;

import com.example.teamtodo.domain.TeamInvite;
import com.example.teamtodo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {

    Optional<TeamInvite> findByInvitee_UserIdAndTeam_TeamId(Long inviteeId, Long teamId);

    List<TeamInvite> findByInvitee(User invitee);

}

