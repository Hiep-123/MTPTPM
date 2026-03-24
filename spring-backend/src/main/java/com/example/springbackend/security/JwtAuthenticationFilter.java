package com.example.springbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);
        try {
            if (!jwtUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            String userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    role != null && role.startsWith("ROLE_") ? role
                            : "ROLE_" + (role != null ? role.toUpperCase() : "USER"));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null,
                    Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ex) {
            // ignore and continue without authentication
        }

        filterChain.doFilter(request, response);
    }
}
