package dev.jade.todolist.services;

import dev.jade.todolist.dto.UserDTO;
import dev.jade.todolist.exceptions.UserAlreadyExistsException;
import dev.jade.todolist.models.User;
import dev.jade.todolist.repositories.UserRepository;
import dev.jade.todolist.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO registerUser(String email, String username, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        User savedUser = userRepository.save(createUser(email, username, password));
        return Mapper.mapToUserDTO(savedUser);
    }

    private User createUser(String email, String username, String password) {
        User user = new User();

        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(Instant.now());

        return user;
    }
}
