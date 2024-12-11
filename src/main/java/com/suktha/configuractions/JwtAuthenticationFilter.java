package com.suktha.configuractions;

import com.suktha.services.jwt.UserService;
import com.suktha.utiles.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);
        final String jwt;
        final String userEmail;
       // if (authHeader == null || !authHeader.startsWith("Bearer "))
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader,"Bearer ")) {
            System.out.println("Authorization header is missing or does not start with 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }
// Extract JWT
        jwt = authHeader.substring(7);
        System.out.println("jwt Token:{}:" + jwt);

        // Extract username
        userEmail = jwtUtil.extractUsername(jwt);
        System.out.println("Extracted Username{}:" + userEmail);

        if (StringUtils.isNoneEmpty(userEmail)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
            System.out.println("Loaded UserDetails: " + userDetails);

            // Validate the token
            boolean isValid = jwtUtil.isTokenValid(jwt, userDetails);
            System.out.println("JWT is valid:  " + isValid);

            if (isValid) {

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
                System.out.println("JWT is valid, authentication set.");
            }
        }
        filterChain.doFilter(request, response);
    }
}