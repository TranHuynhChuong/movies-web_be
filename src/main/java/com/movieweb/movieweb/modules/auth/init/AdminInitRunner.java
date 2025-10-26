package com.movieweb.movieweb.modules.auth.init;

import com.movieweb.movieweb.modules.auth.entity.Account;
import com.movieweb.movieweb.modules.auth.entity.Role;
import com.movieweb.movieweb.modules.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitRunner implements CommandLineRunner {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminInitRunner(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean adminExists = authService.existsByRole(Role.ADMIN);
        if (!adminExists) {
            Account admin = Account.builder()
                    .username(adminUsername)
                    .password(adminPassword)
                    .role(Role.ADMIN)
                    .build();
            authService.createAccount(admin);
            System.out.println("Admin account created!");
        }
    }
}