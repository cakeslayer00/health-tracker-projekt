package dev.techno.health_tracker.service.impl;

import dev.techno.health_tracker.dto.AuthRequestDto;
import dev.techno.health_tracker.dto.AuthResponseDto;
import dev.techno.health_tracker.model.Role;
import dev.techno.health_tracker.model.entity.User;
import dev.techno.health_tracker.repository.UserRepository;
import dev.techno.health_tracker.service.AuthService;
import dev.techno.health_tracker.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDto authenticate(AuthRequestDto authRequestDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDto.username(),
                        authRequestDto.password())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDto(authRequestDto.username(), token);
    }

    @Override
    public void register(AuthRequestDto authRequestDto) {
        User user = new User();
        user.setUsername(authRequestDto.username());
        user.setPassword(passwordEncoder.encode(authRequestDto.password()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

}
