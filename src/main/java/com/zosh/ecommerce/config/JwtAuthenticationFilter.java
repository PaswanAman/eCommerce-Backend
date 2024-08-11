package com.zosh.ecommerce.config;

import com.zosh.ecommerce.serviceImpl.AdminUserDetailService;
import com.zosh.ecommerce.serviceImpl.CustomUserDetailService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private AdminUserDetailService adminUserDetailService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestToken = request.getHeader("Authorization");

        System.out.println(requestToken);

        String username = null;
        String token = null;
        String userType = null;
        if (requestToken != null && requestToken.startsWith("Bearer")) {
            token = requestToken.substring(7);
            try{
                userType = this.jwtTokenHelper.getUserTypeFromToken(token);
                username = this.jwtTokenHelper.getUsernameFromToken(token);

            } catch (IllegalArgumentException e) {
                logger.info("Jwt token has expired");
            } catch (MalformedJwtException e){
                System.out.println("invalid jwt exception");
            }
        } else {
            System.out.println("Token does not being with bearer");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails =  null;
             if ("admin".equals(userType)){
                 userDetails = adminUserDetailService.loadUserByUsername(username);
             }
             else {
                 userDetails =  userDetailsService.loadUserByUsername(username);
             }

            if (this.jwtTokenHelper.validateToken(token, userDetails)) {
                // if everything is fine then
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else {
                // if not
                System.out.println("invalid jwt token");
            }
        } else {
            System.out.println("username is null or context is null");
        }
        filterChain.doFilter(request, response);
    }
}
