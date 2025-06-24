package com.practice.board.repository

import com.practice.board.entity.Post
import com.practice.board.entity.PostLike
import com.practice.board.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLikeRepository : JpaRepository<PostLike, Long> {
    // 특정 사용자가 특정 게시글에 누른 좋아요 정보 조회
    fun findByUserAndPost(user: User, post: Post): PostLike?

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 여부 확인 (존재 여부만)
    fun existsByUserAndPost(user: User, post: Post): Boolean

    // 특정 게시글의 모든 좋아요 삭제 (게시글 삭제 시 사용 가능)
    fun deleteByPost(post: Post)

    // 특정 게시글의 좋아요 개수 카운트
    fun countByPost(post: Post): Long
}
