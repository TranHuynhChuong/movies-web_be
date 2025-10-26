package com.movieweb.movieweb.modules.auth.jwt;

import com.movieweb.movieweb.common.exception.ForbiddenException;
import com.movieweb.movieweb.common.exception.UnauthorizedException;
import com.movieweb.movieweb.modules.auth.annotations.PublicEndpoint;
import com.movieweb.movieweb.modules.auth.annotations.RoleRequired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (method.isAnnotationPresent(RoleRequired.class)) {
            RoleRequired rr = method.getAnnotation(RoleRequired.class);
            String requiredRole = rr.value();

            if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_" + requiredRole))) {
                throw new ForbiddenException();
            }

            return true;
        }

        if (method.isAnnotationPresent(PublicEndpoint.class)) {
            return true;
        }

        if (auth != null && auth.isAuthenticated()) {
            return true;
        }
        throw new UnauthorizedException();
    }
}