package com.practice.board.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "attachments")
@EntityListeners(AuditingEntityListener::class)
class Attachment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post,

    @Column(nullable = false)
    var originalFileName: String,

    @Column(nullable = false, unique = true) // 서버에 저장된 파일명은 유일해야 함
    var storedFileName: String,

    @Column(nullable = false)
    var fileSize: Long,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
