package com.practice.board.dto

import com.practice.board.entity.Comment
import java.time.LocalDateTime

// 댓글 생성 및 수정 요청 DTO
data class CommentRequestDto(
    val content: String
)

// 댓글 응답 DTO
data class CommentResponseDto(
    val id: Long,
    val content: String,
    val authorNickname: String, // 작성자 닉네임
    val postId: Long, // 소속된 게시글 ID
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(comment: Comment): CommentResponseDto {
            return CommentResponseDto(
                id = comment.id!!,
                content = comment.content,
                authorNickname = comment.author.nickname,
                postId = comment.post.id!!,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }
    }
}
