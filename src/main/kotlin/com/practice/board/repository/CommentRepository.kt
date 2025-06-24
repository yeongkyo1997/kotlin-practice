package com.practice.board.repository

import com.practice.board.entity.Comment
import com.practice.board.entity.Post
import com.practice.board.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    // 특정 게시글에 달린 댓글 목록 조회 (페이징 처리 가능하도록 Pageable 추가)
    fun findByPost(post: Post, pageable: Pageable): Page<Comment>

    // 특정 사용자가 작성한 댓글 목록 조회 (페이징 처리)
    fun findByAuthor(author: User, pageable: Pageable): Page<Comment>
}
