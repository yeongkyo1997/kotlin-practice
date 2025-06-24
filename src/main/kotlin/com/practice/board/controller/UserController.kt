package com.practice.board.controller

import com.practice.board.dto.*
import com.practice.board.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getMyInfo(): ResponseEntity<UserResponseDto> {
        val userInfo = userService.getMyInfo()
        return ResponseEntity.ok(userInfo)
    }

    @PutMapping("/me/nickname") // 명세에는 /users/me PUT으로 되어있으나, 닉네임만 변경하는 것이므로 세분화
    fun updateMyNickname(@RequestBody nicknameUpdateRequestDto: NicknameUpdateRequestDto): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateMyNickname(nicknameUpdateRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/me/password") // 명세에는 /users/me PUT으로 되어있으나, 비밀번호만 변경하는 것이므로 세분화
    fun updateMyPassword(@RequestBody passwordUpdateRequestDto: PasswordUpdateRequestDto): ResponseEntity<Void> {
        userService.updateMyPassword(passwordUpdateRequestDto)
        return ResponseEntity.ok().build()
    }

    // 명세서의 /users/me (PUT)은 닉네임과 비밀번호를 한번에 수정하는 것으로 해석될 수 있습니다.
    // 만약 한번의 요청으로 여러 필드를 수정하게 하려면 UserUpdateRequestDto 같은 것을 만들고,
    // 서비스 로직에서 각 필드가 null이 아닐 경우에만 업데이트 하도록 구현할 수 있습니다.
    // 여기서는 명확성을 위해 닉네임과 비밀번호 변경을 분리했습니다.
    // 만약 통합된 PUT /users/me 를 원하시면 수정이 필요합니다.

    @DeleteMapping("/me")
    fun deleteMyAccount(): ResponseEntity<Void> {
        userService.deleteMyAccount()
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/me/posts")
    fun getMyPosts(@PageableDefault(size = 10, sort = ["createdAt,desc"]) pageable: Pageable): ResponseEntity<Page<PostSimpleResponseDto>> {
        val postsPage = userService.getMyPosts(pageable)
        return ResponseEntity.ok(postsPage)
    }

    @GetMapping("/me/comments")
    fun getMyComments(@PageableDefault(size = 10, sort = ["createdAt,desc"]) pageable: Pageable): ResponseEntity<Page<CommentResponseDto>> {
        val commentsPage = userService.getMyComments(pageable)
        return ResponseEntity.ok(commentsPage)
    }
}
