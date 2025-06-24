package com.practice.board.dto

import com.practice.board.entity.Post
import com.practice.board.entity.User
import java.time.LocalDateTime

// 게시글 생성 및 수정 요청 DTO
data class PostRequestDto(
    val title: String,
    val content: String,
    val category: String? = null // 카테고리는 선택 사항
)

// 게시글 단건 상세 응답 DTO
data class PostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val authorNickname: String, // 작성자 닉네임
    val viewCount: Long,
    val category: String?,
    val likeCount: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(post: Post): PostResponseDto {
            return PostResponseDto(
                id = post.id!!,
                title = post.title,
                content = post.content,
                authorNickname = post.author.nickname, // User 엔티티에서 닉네임 가져오기
                viewCount = post.viewCount,
                category = post.category,
                likeCount = post.likeCount,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
        }
    }
}

// 게시글 목록의 각 아이템 DTO (간단 정보)
data class PostSimpleResponseDto(
    val id: Long,
    val title: String,
    val authorNickname: String,
    val viewCount: Long,
    val category: String?,
    val likeCount: Long,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(post: Post): PostSimpleResponseDto {
            return PostSimpleResponseDto(
                id = post.id!!,
                title = post.title,
                authorNickname = post.author.nickname,
                viewCount = post.viewCount,
                category = post.category,
                likeCount = post.likeCount,
                createdAt = post.createdAt
            )
        }
    }
}
