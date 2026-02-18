package dev.jade.todolist.services;

import dev.jade.todolist.dtos.requests.AuthRequest;
import dev.jade.todolist.dtos.requests.LoginRequest;
import dev.jade.todolist.dtos.responses.AuthResponse;
import dev.jade.todolist.exceptions.EntityNotFoundException;
import dev.jade.todolist.exceptions.UserAlreadyExistsException;
import dev.jade.todolist.mapstruct.mappers.UserMapper;
import dev.jade.todolist.models.User;
import dev.jade.todolist.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper mapper;

    @Transactional
    public void registerUser(AuthRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }
        User createdUser = mapper.toEntity(request);
        createdUser.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(createdUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        establishSession(authentication, httpRequest);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User", "email", request.email()));

        return mapper.toResponse(user);
    }

    public AuthResponse loginUser(AuthRequest request, HttpServletRequest httpRequest) {
        return loginUser(new LoginRequest(request.email(), request.password()), httpRequest);
    }

    public void establishSession(Authentication authentication, HttpServletRequest httpRequest) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );
    }
}
