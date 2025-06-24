package com.practice.board.service

import com.practice.board.dto.LoginRequestDto
import com.practice.board.dto.SignupRequestDto
import com.practice.board.dto.TokenResponseDto
import com.practice.board.entity.User
import com.practice.board.entity.Role
import com.practice.board.repository.UserRepository
import com.practice.board.security.JwtTokenProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val customUserDetailsService: CustomUserDetailsService // 직접 주입받기보다는 JwtTokenProvider가 내부적으로 사용
) {

    @Transactional
    fun signup(signupRequestDto: SignupRequestDto): User {
        if (userRepository.findByUsername(signupRequestDto.username) != null) {
            throw IllegalArgumentException("Username already exists")
        }

        val user = User(
            username = signupRequestDto.username,
            password = passwordEncoder.encode(signupRequestDto.password),
            nickname = signupRequestDto.nickname,
            role = Role.USER // 기본 역할은 USER
        )
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun login(loginRequestDto: LoginRequestDto): TokenResponseDto {
        val user = userRepository.findByUsername(loginRequestDto.username)
            ?: throw BadCredentialsException("Invalid username or password")

        if (!passwordEncoder.matches(loginRequestDto.password, user.password)) {
            throw BadCredentialsException("Invalid username or password")
        }

        // JWT 생성 (CustomUserDetailsService를 통해 UserDetails를 가져와서 역할을 설정할 수도 있음)
        // 여기서는 User 엔티티의 role 정보를 직접 사용
        val token = jwtTokenProvider.createToken(user.username, listOf(user.role))

        return TokenResponseDto(accessToken = token)
    }
}
