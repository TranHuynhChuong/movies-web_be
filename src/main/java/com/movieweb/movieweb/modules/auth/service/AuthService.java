package com.movieweb.movieweb.modules.auth.service;

import com.movieweb.movieweb.common.exception.BadRequestException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.modules.auth.entity.Account;
import com.movieweb.movieweb.modules.auth.jwt.JwtUtils;
import com.movieweb.movieweb.modules.auth.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public String login(String username, String password) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadRequestException("Mật khẩu không đúng");
        }
        return jwtService.generateToken(account.getId(), account.getUsername(), account.getRole().name());
    }
}