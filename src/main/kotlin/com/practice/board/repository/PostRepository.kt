package com.practice.board.repository

import com.practice.board.entity.Post
import com.practice.board.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    // 특정 사용자가 작성한 게시글 목록 조회 (페이징 처리)
    fun findByAuthor(author: User, pageable: Pageable): Page<Post>

    // 카테고리별 게시글 목록 조회 (페이징 처리)
    fun findByCategory(category: String, pageable: Pageable): Page<Post>
}
