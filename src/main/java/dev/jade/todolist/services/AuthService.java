package dev.jade.todolist.services;

import dev.jade.todolist.dtos.requests.AuthRequest;
import dev.jade.todolist.dtos.responses.AuthResponse;
import dev.jade.todolist.exceptions.UserAlreadyExistsException;
import dev.jade.todolist.mapstruct.mappers.UserMapper;
import dev.jade.todolist.models.User;
import dev.jade.todolist.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public AuthResponse createUser(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new UserAlreadyExistsException("Email already exists");

        User createdUser = mapper.toEntity(request);
        createdUser.setPassword(passwordEncoder.encode(request.getPassword()));

        return mapper.toResponse(userRepository.save(createdUser));
    }
}
