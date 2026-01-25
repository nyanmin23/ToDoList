package dev.jade.todolist.utils;

import dev.jade.todolist.dto.UserDTO;
import dev.jade.todolist.models.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Mapper {
    public static UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setCreatedAt(user.getCreatedAt());

        return userDTO;
    }
}
