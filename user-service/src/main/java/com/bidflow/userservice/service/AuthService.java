package com.bidflow.userservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bidflow.userservice.dto.LoginRequestDTO;
import com.bidflow.userservice.dto.LoginResponseDTO;
import com.bidflow.userservice.dto.RegisterRequestDTO;
import com.bidflow.userservice.dto.RegisterResponseDTO;
import com.bidflow.userservice.entity.User;
import com.bidflow.userservice.exception.InvalidCredentialsException;
import com.bidflow.userservice.exception.UserAlreadyExistsException;
import com.bidflow.userservice.repository.UserRepository;
import com.bidflow.userservice.util.JwtUtil;
import com.bidflow.userservice.util.UserRole;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException(
                    "USER_ALREADY_EXISTS_WITH_THIS_EMAIL: " + registerRequestDTO.getEmail());
        }

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(UserRole.valueOf(registerRequestDTO.getRole()));

        User userSaved = userRepository.save(user);

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();
        registerResponseDTO.setName(userSaved.getName());
        registerResponseDTO.setEmail(userSaved.getEmail());
        registerResponseDTO.setRole(userSaved.getRole().toString());
        registerResponseDTO.setId(userSaved.getId().toString());

        return registerResponseDTO;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // check if user is there or not with this email in db
        // match passwords
        // generate token and send token

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtUtil.generateToken(user.getId().toString(), loginRequestDTO.getEmail(), user.getRole().toString());

        return new LoginResponseDTO(accessToken, user.getRole().toString(), user.getId().toString());

    }

}
