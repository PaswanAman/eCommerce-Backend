package com.zosh.ecommerce.config;

import com.zosh.ecommerce.serviceImpl.AdminUserDetailService;
import com.zosh.ecommerce.serviceImpl.CustomUserDetailService;
//import lombok.var;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig extends WebMvcAutoConfiguration {

    @Autowired
    private CustomUserDetailService customerUserDetailService;


    @Autowired
    private AdminUserDetailService adminUserDetailService;


    @Autowired
     private JwtAuthenticationFilter filter;

    public SecurityConfig(JwtAuthenticationFilter filter){
        this.filter = filter;
    }

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ModelMapper modelMapper(){
    return new ModelMapper();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
         return new ProviderManager(Arrays.asList(userAuthenticationProvider(),adminAuthenticationProvider()));
    }


    @Bean
    public AuthenticationProvider userAuthenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customerUserDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(adminUserDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/api/v1/auth/buyer/**",
                                        "/api/v1/auth/picture/**",
                                        "/api/v1/auth/admin/**",
                                        "/api/v1/user/non-expired",
                                        "/api/v1/user/allcategories",
                                        "/api/v1/auth/seller/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/user/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/user/SingleUser").authenticated()
                        .requestMatchers("/api/v1/user/seller/**","/api/v1/auth/changePassword").hasAnyAuthority("ROLE_SELLER")
                        .requestMatchers("/api/v1/user/product/{productId}").authenticated()
                        .requestMatchers("/api/v1/user/cart/buyer/**","/api/v1/user/buyer/**","/api/v1/auth/changePassword").hasAnyAuthority("ROLE_BUYER")
                        .requestMatchers("/api/v1/user/cart/{userId}").authenticated()
                         )
                .exceptionHandling(ex-> ex.authenticationEntryPoint(point))
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
