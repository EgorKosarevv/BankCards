package org.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {


            log.debug(" JwtAuthFilter: request {}", request.getRequestURI());
            log.debug(" Headers: {}", Collections.list(request.getHeaderNames()));

            String authHeader = request.getHeader("Authorization");


            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No Authorization header found or it does not start with 'Bearer '");
                chain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String username = extractUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(username, token, request);
            }

            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            log.error("JWT token expired: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"The token has expired, please get a new token.\"}");
            response.getWriter().flush();
        }
    }

    private String extractUsernameFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        log.debug("Extracted username from JWT: {}", username);
        return username;
    }

    private void authenticateUser(String username, String token, HttpServletRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (jwtUtil.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("User {} successfully authenticated", username);
        } else {
            log.warn("JWT token validation failed for user: {}", username);
        }
    }
}
