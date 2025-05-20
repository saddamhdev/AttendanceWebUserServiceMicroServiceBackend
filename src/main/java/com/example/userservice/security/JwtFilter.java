package com.example.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtGenerator jwtUtil;
    private final UserDetailsService userDetailsService;
    private  final CustomUserDetailsService customUserDetailsService;
    public JwtFilter(JwtGenerator jwtUtil, UserDetailsService userDetailsService,CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.customUserDetailsService=customUserDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain chain) throws ServletException, IOException {
        // System.out.println("Incoming Request Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(header ->{

                    //  System.out.println(header + ": " + request.getHeader(header))
                }

        );

        final String authHeader = request.getHeader("Authorization");
        // System.out.println("Auth Header Received: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            //  System.out.println("Missing or Invalid Token");
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);



        if(jwtUtil.isTokenValid(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String username = jwtUtil.extractUsername(token);
        // check valid Refresh token exist or not

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                // System.out.println("User Authenticated: " + username);
            } else {
                // System.out.println("Invalid Token");
            }
        }

        chain.doFilter(request, response);
    }

}
