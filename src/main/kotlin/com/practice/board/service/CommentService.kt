package com.practice.board.service

import com.practice.board.dto.CommentRequestDto
import com.practice.board.dto.CommentResponseDto
import com.practice.board.entity.Comment
import com.practice.board.entity.User
import com.practice.board.repository.CommentRepository
import com.practice.board.repository.PostRepository
import com.practice.board.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {

    private fun getCurrentUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            ?: throw IllegalStateException("User not found, authentication error")
    }

    @Transactional
    fun createComment(postId: Long, commentRequestDto: CommentRequestDto): CommentResponseDto {
        val currentUser = getCurrentUser()
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId") }

        val comment = Comment(
            content = commentRequestDto.content,
            author = currentUser,
            post = post
        )
        val savedComment = commentRepository.save(comment)
        return CommentResponseDto.fromEntity(savedComment)
    }

    @Transactional(readOnly = true)
    fun getCommentsByPostId(postId: Long, pageable: Pageable): Page<CommentResponseDto> {
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId to retrieve comments") }
        return commentRepository.findByPost(post, pageable).map { CommentResponseDto.fromEntity(it) }
    }

    @Transactional
    fun updateComment(commentId: Long, commentRequestDto: CommentRequestDto): CommentResponseDto {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { NoSuchElementException("Comment not found with ID: $commentId") }
        val currentUser = getCurrentUser()

        if (comment.author.id != currentUser.id) {
            throw SecurityException("User does not have permission to update this comment")
        }

        comment.content = commentRequestDto.content
        val updatedComment = commentRepository.save(comment)
        return CommentResponseDto.fromEntity(updatedComment)
    }

    @Transactional
    fun deleteComment(commentId: Long) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { NoSuchElementException("Comment not found with ID: $commentId") }
        val currentUser = getCurrentUser()

        // 본인 댓글 또는 관리자만 삭제 가능
        if (comment.author.id != currentUser.id && currentUser.role != com.practice.board.entity.Role.ADMIN) {
            throw SecurityException("User does not have permission to delete this comment")
        }
        commentRepository.delete(comment)
    }
}
