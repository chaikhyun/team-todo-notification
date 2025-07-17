package com.example.teamtodo.repository;

import com.example.teamtodo.domain.Team;
import com.example.teamtodo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findByTeam(Team team);

}
