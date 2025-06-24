package com.practice.board.repository

import com.practice.board.entity.Attachment
import com.practice.board.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository : JpaRepository<Attachment, Long> {
    // 특정 게시글에 첨부된 모든 파일 조회
    fun findAllByPost(post: Post): List<Attachment>

    // 저장된 파일명으로 첨부파일 정보 조회
    fun findByStoredFileName(storedFileName: String): Attachment?

    // 특정 게시글에 첨부된 모든 파일 삭제 (게시글 삭제 시 사용 가능)
    fun deleteAllByPost(post: Post)
}
