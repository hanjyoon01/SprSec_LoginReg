package com.example.seven.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
public class SecurityConfig {

    // 비밀번호 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // role hierarchy
    @Bean
    public RoleHierarchy roleHierarchy() {
        // ADMIN이 USER보다 상위 권한임을 표시
        return RoleHierarchyImpl.withRolePrefix("")
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userService) {

        // CSRF disable
        http
                .csrf(csrf -> csrf.disable());
//                .csrf(Customizer.withDefaults());
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/logout"));

        // 로그인 시큐리티 필터 설정
        http
                .formLogin(login -> login
                        .loginProcessingUrl("/login")
                        .loginPage("/login"));

        // 로그인 유지 설정 (rememberMe)
        http
                .rememberMe(me -> me
                        .key("qdflaksfnalkadlknasldknaadagghmksdfw")
                        .rememberMeParameter("remember-me")
                        // 1일 동안 유지
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userService)
                );

        // 로그아웃 설정
        http.logout((auth) -> auth
                .logoutUrl("/logout")       // HTML에서 요청할 로그아웃 주소
                .logoutSuccessUrl("/")      // 로그아웃 성공 후 메인 홈 화면으로 이동!
                .permitAll()
        );

        // 권한별 인가 필터
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/join").permitAll()
                        .requestMatchers("/login").permitAll()
                        // 홈, 회원가입, 로그인 경로는 모든 권한 접근 가능
                        .requestMatchers("/user/**").hasAuthority("USER")
                        // 관리자 페이지는 관리자 권한만 접근 가능
                        .requestMatchers("/admin").access(customAuthorizationManager())
                        // 그 외 요청 모두 거절
                        .anyRequest().denyAll()
                );

        // 세션
//        http
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그인 이후 쿠키로 발급되는 JSESSIONID가 변경됨
        http
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId());


        // 최종 빌드
        return http.build();
    }

    private AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager() {
        return (authentication, context) -> {
            boolean allowed =
                authentication.get().getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ADMIN"));
            // 지역 맞는지

            // 사용할 수 있는 카운트

            // 비즈니스, 개인

            return new AuthorizationDecision(allowed);
        };
    }
}
