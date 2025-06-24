package com.practice.board.dto

import com.practice.board.entity.User
import java.time.LocalDateTime

// 내 정보 응답 DTO
data class UserResponseDto(
    val id: Long,
    val username: String,
    val nickname: String,
    val role: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(user: User): UserResponseDto {
            return UserResponseDto(
                id = user.id!!,
                username = user.username,
                nickname = user.nickname,
                role = user.role.name,
                createdAt = user.createdAt
            )
        }
    }
}

// 닉네임 변경 요청 DTO
data class NicknameUpdateRequestDto(
    val newNickname: String
)

// 비밀번호 변경 요청 DTO
data class PasswordUpdateRequestDto(
    val currentPassword: String,
    val newPassword: String
)
