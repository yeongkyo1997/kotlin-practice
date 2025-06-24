package com.practice.board.service

import com.practice.board.dto.PostRequestDto
import com.practice.board.dto.PostResponseDto
import com.practice.board.dto.PostSimpleResponseDto
import com.practice.board.entity.Post
import com.practice.board.entity.User
import com.practice.board.repository.PostRepository
import com.practice.board.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.criteria.Predicate

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository // 작성자 정보를 가져오기 위해
) {

    private fun getCurrentUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            ?: throw IllegalStateException("User not found, authentication error")
    }

    @Transactional
    fun createPost(postRequestDto: PostRequestDto): PostResponseDto {
        val currentUser = getCurrentUser()
        val post = Post(
            title = postRequestDto.title,
            content = postRequestDto.content,
            author = currentUser,
            category = postRequestDto.category
            // viewCount, likeCount는 기본값 0으로 설정됨
        )
        val savedPost = postRepository.save(post)
        return PostResponseDto.fromEntity(savedPost)
    }

    @Transactional(readOnly = true)
    fun getPostById(postId: Long): PostResponseDto {
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId") }
        // TODO: 조회수 증가 로직 추가 (예: 별도 트랜잭션 또는 비동기 처리)
        return PostResponseDto.fromEntity(post)
    }

    @Transactional(readOnly = true)
    fun getAllPosts(pageable: Pageable, category: String?, keyword: String?): Page<PostSimpleResponseDto> {
        // Specification을 사용한 동적 쿼리
        val spec = Specification<Post> { root, query, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()
            category?.let {
                predicates.add(criteriaBuilder.equal(root.get<String>("category"), it))
            }
            keyword?.let {
                val keywordPattern = "%${it.lowercase()}%"
                predicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywordPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), keywordPattern)
                    )
                )
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        return postRepository.findAll(spec, pageable).map { PostSimpleResponseDto.fromEntity(it) }
    }


    @Transactional
    fun updatePost(postId: Long, postRequestDto: PostRequestDto): PostResponseDto {
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId") }
        val currentUser = getCurrentUser()

        if (post.author.id != currentUser.id) {
            throw SecurityException("User does not have permission to update this post")
        }

        post.title = postRequestDto.title
        post.content = postRequestDto.content
        post.category = postRequestDto.category
        // updatedAt은 Auditing에 의해 자동 업데이트

        val updatedPost = postRepository.save(post)
        return PostResponseDto.fromEntity(updatedPost)
    }

    @Transactional
    fun deletePost(postId: Long) {
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId") }
        val currentUser = getCurrentUser()

        if (post.author.id != currentUser.id && currentUser.role != com.practice.board.entity.Role.ADMIN) { // 관리자도 삭제 가능하도록
            throw SecurityException("User does not have permission to delete this post")
        }
        // TODO: 연관된 댓글, 좋아요, 첨부파일 삭제 로직 추가 (Cascade 설정 또는 명시적 삭제)
        postRepository.delete(post)
    }

    // 조회수 증가 (별도 트랜잭션으로 분리 고려)
    @Transactional
    fun incrementViewCount(postId: Long) {
        postRepository.findById(postId).ifPresent { post ->
            post.viewCount += 1
            postRepository.save(post)
        }
    }
}
