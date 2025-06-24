package com.practice.board.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // rest api 만을 고려하여 기본 설정은 해제
            .csrf { it.disable() } // csrf 보안 토큰 disable 처리.
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login").permitAll() // 회원가입, 로그인은 누구나 접근 가능
                    .requestMatchers("/api/v1/posts", "/api/v1/posts/{postId}").permitAll() // 게시글 목록 및 상세 조회는 누구나 접근 가능 (GET 요청에 대해서만)
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 접근 허용
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 관리자 기능은 ADMIN 역할 필요
                    .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다

        return http.build()
    }
}
