package com.practice.board.dto

// 회원가입 요청 DTO
data class SignupRequestDto(
    val username: String,
    val password: String,
    val nickname: String
)

// 로그인 요청 DTO
data class LoginRequestDto(
    val username: String,
    val password: String
)

// JWT 토큰 응답 DTO
data class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String? = null // 선택적으로 리프레시 토큰도 포함 가능
)
