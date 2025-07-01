package com.salon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form

                        .loginPage("/login") // 커스텀 로그인 페이지 주소
                        .loginProcessingUrl("/login") // form action
                        .failureUrl("/login?error")// 로그인실패시 어떻게?
                        .usernameParameter("loginId") // input name
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true) // 성공시 리다이렉트
                        .permitAll() //로그인 페이지 모두에게 접속 허용
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("unique-remember-me-key")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60 * 60 * 24 * 15)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // 로그아웃 성공시 메인페이지 이동
                        .invalidateHttpSession(true) // 로그아웃시 회원 세션 모두 삭제
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/css/**", "/images/**", "/javascript/**").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 권한
                        .anyRequest().permitAll()
                )


                .csrf(
                        cr ->
                                cr.csrfTokenRepository(
                                                CookieCsrfTokenRepository.withHttpOnlyFalse())
                );

        //http.formLogin(Customizer.withDefaults());


        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




}
