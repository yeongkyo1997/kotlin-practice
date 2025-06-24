package com.practice.board.service

import com.practice.board.entity.User
import com.practice.board.repository.PostLikeRepository
import com.practice.board.repository.PostRepository
import com.practice.board.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostLikeService(
    private val postLikeRepository: PostLikeRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {

    private fun getCurrentUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            ?: throw IllegalStateException("User not found, authentication error")
    }

    @Transactional
    fun togglePostLike(postId: Long): Map<String, Any> {
        val currentUser = getCurrentUser()
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId") }

        val existingLike = postLikeRepository.findByUserAndPost(currentUser, post)

        if (existingLike != null) {
            // 이미 좋아요를 누른 경우, 좋아요 취소
            postLikeRepository.delete(existingLike)
            post.likeCount = postLikeRepository.countByPost(post) // likeCount 갱신
            postRepository.save(post)
            return mapOf("liked" to false, "likeCount" to post.likeCount)
        } else {
            // 좋아요를 누르지 않은 경우, 좋아요 추가
            val newLike = com.practice.board.entity.PostLike(user = currentUser, post = post)
            postLikeRepository.save(newLike)
            post.likeCount = postLikeRepository.countByPost(post) // likeCount 갱신
            postRepository.save(post)
            return mapOf("liked" to true, "likeCount" to post.likeCount)
        }
    }

    @Transactional(readOnly = true)
    fun getLikeState(postId: Long): Map<String, Any> {
        val currentUser = getCurrentUser() // 인증된 사용자인지 확인
        val post = postRepository.findById(postId)
            .orElseThrow { NoSuchElementException("Post not found with ID: $postId") }

        val liked = postLikeRepository.existsByUserAndPost(currentUser, post)
        return mapOf("liked" to liked, "likeCount" to post.likeCount)
    }
}
