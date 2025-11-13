package dev.techno.health_tracker.service;

import dev.techno.health_tracker.dto.AuthRequestDto;
import dev.techno.health_tracker.dto.AuthResponseDto;

public interface AuthService {

    AuthResponseDto authenticate(AuthRequestDto authRequestDto);

    void register(AuthRequestDto authRequestDto);

}
