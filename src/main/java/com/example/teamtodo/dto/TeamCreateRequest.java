package com.example.teamtodo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequest {

    @NotBlank(message = "팀 이름은 필수입니다.")
    private String name;
}
