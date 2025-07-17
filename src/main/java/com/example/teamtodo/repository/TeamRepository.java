package com.example.teamtodo.repository;

import com.example.teamtodo.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // JpaRepository 상속 시 기본적으로 findById 메서드 제공
}
