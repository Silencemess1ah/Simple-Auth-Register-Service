package com.shnordiq.service;

import com.shnordiq.dto.UserRegistrationDto;
import com.shnordiq.exception.UserAlreadyExistsException;
import com.shnordiq.model.Authority;
import com.shnordiq.model.User;
import com.shnordiq.model.enums.UserRole;
import com.shnordiq.repository.AuthorityRepository;
import com.shnordiq.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerNewUser(UserRegistrationDto userDto) {
        if (checkEmailAndNicknameExists(userDto.getNickname(), userDto.getEmail())) {
            log.warn("Such nickname {} or email: {} already taken!", userDto.getNickname(), userDto.getEmail());
            throw new UserAlreadyExistsException("User with such nickname or email is already exists");
        }
        Authority authority =Authority.builder()
                .role(UserRole.ROLE_USER)
                .build();

        User newUser = User.builder()
                .nickname(userDto.getNickname())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .enabled(true)
                .email(userDto.getEmail())
                .authority(authority)
                .build();

        authority.setUser(newUser);

        log.debug("Saving new user to DB");
        User user = userRepository.save(newUser);
        log.debug("Successfully saved new user");
        //todo kafka send to user that account successfully registered
    }

    private boolean checkEmailAndNicknameExists(String nickname, String email) {
        return (userRepository.existsByNickname(nickname) && userRepository.existsByEmail(email));
    }

    public void grantNewRole(UserRole role) {
        Authority authority = getAuthorityContext();
        authority.setRole(role);
        authorityRepository.save(authority);
        //todo kafka send notification that granted a new role
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Authenticated user with email "
                            + email + " not found"));
            return user.getUserId();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
    }

    private Authority getAuthorityContext() {
        UUID userId = getAuthenticatedUserId();
        return authorityRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Authority associated with user_id "
                        + userId + " not found"));
    }

    private User getUserContext() {
        UUID userId = getAuthenticatedUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id "
                        + userId + " not found"));
    }
}
