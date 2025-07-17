package com.example.teamtodo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamDetailResponse {

    private Long teamId;
    private String teamName;

    private LeaderInfo leader;
    private List<MemberInfo> members;
    private int memberCount;

    private List<TodoInfo> todos;

    @Data
    @Builder
    public static class LeaderInfo {
        private Long userId;
        private String username;
        private String email;
    }

    @Data
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String username;
        private String role;
    }

    @Data
    @Builder
    public static class TodoInfo {
        private Long assigneeId;
        private String assigneeName;
        private String title;
        private String status;
        private String dueDate;
    }
}
