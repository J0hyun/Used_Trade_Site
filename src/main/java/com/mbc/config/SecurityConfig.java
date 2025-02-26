package com.mbc.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("----------SecurityFilterChain 진입 체크----------");

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/member/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("name")
                        .failureUrl("/login/error")
                )
                .oauth2Login(oauth2->oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login/error")
                )

                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/item/delete") // delete 요청에 대한 CSRF 보호 비활성화
                        .ignoringRequestMatchers("/get-user-name") // 이 엔드포인트는 누구나 접근 가능하도록 설정
                );

//        http.formLogin()
//                .loginPage("/login")
//                .permitAll()
//                .and()
//                .exceptionHandling()
//                .accessDeniedPage("/access-denied"); // 접근 거부 시 리다이렉트될 페이지

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
