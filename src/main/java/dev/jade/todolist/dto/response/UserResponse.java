package dev.jade.todolist.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

}
