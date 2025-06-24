package com.practice.board.controller

import com.practice.board.dto.CommentRequestDto
import com.practice.board.dto.CommentResponseDto
import com.practice.board.service.CommentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments") // 게시글 ID를 경로에 포함
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    fun createComment(
        @PathVariable postId: Long,
        @RequestBody commentRequestDto: CommentRequestDto
    ): ResponseEntity<CommentResponseDto> {
        val commentResponse = commentService.createComment(postId, commentRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse)
    }

    @GetMapping
    fun getCommentsByPostId(
        @PathVariable postId: Long,
        @PageableDefault(size = 10, sort = ["createdAt,asc"]) pageable: Pageable // 댓글은 보통 오래된 순
    ): ResponseEntity<Page<CommentResponseDto>> {
        val commentsPage = commentService.getCommentsByPostId(postId, pageable)
        return ResponseEntity.ok(commentsPage)
    }

    // 댓글 ID로 직접 접근하는 API (수정, 삭제)
    // 경로는 /api/v1/comments/{commentId} 와 같이 별도로 만들 수도 있으나,
    // 여기서는 일관성을 위해 /api/v1/posts/{postId}/comments/{commentId} 유지
    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable postId: Long, // 경로 일관성을 위해 받지만, 실제 서비스 로직에서는 commentId로 조회하므로 사용 안 할 수 있음
        @PathVariable commentId: Long,
        @RequestBody commentRequestDto: CommentRequestDto
    ): ResponseEntity<CommentResponseDto> {
        // postId를 사용하여 댓글이 해당 게시글에 속하는지 검증하는 로직을 추가할 수 있음
        val updatedComment = commentService.updateComment(commentId, commentRequestDto)
        return ResponseEntity.ok(updatedComment)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable postId: Long, // 경로 일관성
        @PathVariable commentId: Long
    ): ResponseEntity<Void> {
        // postId를 사용하여 댓글이 해당 게시글에 속하는지 검증하는 로직을 추가할 수 있음
        commentService.deleteComment(commentId)
        return ResponseEntity.noContent().build()
    }
}
