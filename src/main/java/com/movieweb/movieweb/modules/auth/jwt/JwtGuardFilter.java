package com.movieweb.movieweb.modules.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class JwtGuardFilter extends OncePerRequestFilter {

    private final JwtUtils jwtService;
    private final ObjectMapper objectMapper;

    public JwtGuardFilter(JwtUtils jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        try {

            // Lấy token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            Claims claims;
            claims = jwtService.extractAllClaims(token);

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            sendError(response, 401, "Token không hợp lệ hoặc hết hạn");
        }
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        ApiResponse<Object> body = ResponseHelper.error(statusCode, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private Method getHandlerMethod(HttpServletRequest request) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handler instanceof HandlerMethod hm) {
            return hm.getMethod();
        }
        return null;
    }
}