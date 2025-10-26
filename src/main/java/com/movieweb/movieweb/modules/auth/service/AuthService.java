package com.movieweb.movieweb.modules.auth.service;

import com.movieweb.movieweb.common.exception.BadRequestException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.modules.auth.dto.LoginResponseDto;
import com.movieweb.movieweb.modules.auth.dto.UserDto;
import com.movieweb.movieweb.modules.auth.entity.Account;
import com.movieweb.movieweb.modules.auth.entity.Role;
import com.movieweb.movieweb.modules.auth.jwt.JwtUtils;
import com.movieweb.movieweb.modules.auth.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtService;

    public AuthService(AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(String username, String password, Role role) {
        username = username.trim();

        Account account = accountRepository
                .findByUsernameAndRole(username, role)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadRequestException("Mật khẩu không đúng");
        }

        String token = jwtService.generateToken(
                account.getId(),
                account.getUsername(),
                account.getRole().name()
        );

        long expiresIn = jwtService.getExpiration();

        UserDto userDto = UserDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .role(account.getRole().name())
                .build();

        return LoginResponseDto.builder()
                .user(userDto)
                .accessToken(token)
                .expiresIn(expiresIn)
                .build();
    }
    public UserDto updateAccount(String accountId, String newUsername, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));

        newUsername = newUsername.trim();
        if (!account.getUsername().equals(newUsername)) {
            boolean exists = accountRepository.existsByUsername(newUsername);
            if (exists) {
                throw new BadRequestException("Username đã tồn tại");
            }
            account.setUsername(newUsername);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            account.setPassword(passwordEncoder.encode(newPassword));
        }

        accountRepository.save(account);

        return UserDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .role(account.getRole().name())
                .build();
    }

    public boolean existsByRole(Role role) {
        return accountRepository.existsByRole(role);
    }

    public void createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }
}