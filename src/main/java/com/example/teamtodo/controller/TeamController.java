package com.example.teamtodo.controller;

import com.example.teamtodo.domain.User;
import com.example.teamtodo.dto.TeamCreateRequest;
import com.example.teamtodo.dto.TeamResponse;
import com.example.teamtodo.exception.NotFoundException;
import com.example.teamtodo.repository.UserRepository;
import com.example.teamtodo.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;  // ✅ 추가

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
}
