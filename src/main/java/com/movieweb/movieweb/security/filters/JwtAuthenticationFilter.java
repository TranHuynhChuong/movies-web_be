package com.movieweb.movieweb.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieweb.movieweb.common.dto.ApiResponse;
import com.movieweb.movieweb.common.dto.ResponseHelper;
import com.movieweb.movieweb.common.exception.ForbiddenException;
import com.movieweb.movieweb.common.exception.UnauthorizedException;
import com.movieweb.movieweb.security.annotations.RoleRequired;
import com.movieweb.movieweb.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractAllClaims(jwt);
            String id = claims.getSubject();
            String role = claims.get("role", String.class);
            String username = claims.get("username", String.class);

            if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var authToken = new UsernamePasswordAuthenticationToken(id, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            Method method = getHandlerMethod(request);
            if (method != null) {
                RoleRequired roleRequired = method.getAnnotation(RoleRequired.class);
                if (roleRequired != null && !roleRequired.value().equals(role)) {
                    throw new ForbiddenException();
                }
            }



        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw new UnauthorizedException("Token không hợp lệ hoặc hết hạn");
        }

        chain.doFilter(request, response);
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