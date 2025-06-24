package com.practice.board.service

import com.practice.board.dto.*
import com.practice.board.entity.User
import com.practice.board.repository.CommentRepository
import com.practice.board.repository.PostRepository
import com.practice.board.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private fun getCurrentAuthenticatedUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            ?: throw IllegalStateException("Authenticated user not found.")
    }

    @Transactional(readOnly = true)
    fun getMyInfo(): UserResponseDto {
        val currentUser = getCurrentAuthenticatedUser()
        return UserResponseDto.fromEntity(currentUser)
    }

    @Transactional
    fun updateMyNickname(nicknameUpdateRequestDto: NicknameUpdateRequestDto): UserResponseDto {
        val currentUser = getCurrentAuthenticatedUser()
        // 닉네임 중복 검사 (선택 사항 - 요구사항에 따라)
        // userRepository.findByNickname(nicknameUpdateRequestDto.newNickname)?.let {
        //     if (it.id != currentUser.id) throw IllegalArgumentException("Nickname already in use.")
        // }
        currentUser.nickname = nicknameUpdateRequestDto.newNickname
        userRepository.save(currentUser)
        return UserResponseDto.fromEntity(currentUser)
    }

    @Transactional
    fun updateMyPassword(passwordUpdateRequestDto: PasswordUpdateRequestDto) {
        val currentUser = getCurrentAuthenticatedUser()
        if (!passwordEncoder.matches(passwordUpdateRequestDto.currentPassword, currentUser.password)) {
            throw IllegalArgumentException("Current password does not match.")
        }
        if (passwordUpdateRequestDto.newPassword.length < 4) { // 예시: 최소 길이 검증
             throw IllegalArgumentException("New password is too short.")
        }
        currentUser.password = passwordEncoder.encode(passwordUpdateRequestDto.newPassword)
        userRepository.save(currentUser)
    }

    @Transactional
    fun deleteMyAccount() {
        val currentUser = getCurrentAuthenticatedUser()
        // 사용자 계정 삭제 로직 (soft delete 또는 hard delete)
        // 여기서는 hard delete 예시. 연관 데이터 처리 필요 (게시글, 댓글 등)
        // TODO: 사용자가 작성한 게시글, 댓글, 좋아요 등 처리 방안 결정 필요
        // 1. 사용자 null로 변경 (작성자 정보 유지)
        // 2. 함께 삭제 (CASCADE)
        // 3. 탈퇴 시점에 특정 상태로 변경 (예: '탈퇴한 사용자')
        // 우선은 사용자만 삭제. 실제 운영 시에는 연관 데이터 처리 필수.
        userRepository.delete(currentUser)
    }

    @Transactional(readOnly = true)
    fun getMyPosts(pageable: Pageable): Page<PostSimpleResponseDto> {
        val currentUser = getCurrentAuthenticatedUser()
        return postRepository.findByAuthor(currentUser, pageable).map { PostSimpleResponseDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getMyComments(pageable: Pageable): Page<CommentResponseDto> {
        val currentUser = getCurrentAuthenticatedUser()
        return commentRepository.findByAuthor(currentUser, pageable).map { CommentResponseDto.fromEntity(it) }
    }
}
