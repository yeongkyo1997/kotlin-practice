package com.practice.board.controller

import com.practice.board.dto.LoginRequestDto
import com.practice.board.dto.SignupRequestDto
import com.practice.board.dto.TokenResponseDto
import com.practice.board.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody signupRequestDto: SignupRequestDto): ResponseEntity<Any> {
        // 사용자 DTO를 반환하거나, 성공 메시지를 반환할 수 있습니다.
        // 여기서는 생성된 사용자 정보를 그대로 반환 (비밀번호는 제외하거나 UserResponseDto를 만들어 사용 권장)
        // 간단하게 하기 위해 여기서는 ID만 반환하는 형태로 가정
        val user = authService.signup(signupRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("id" to user.id, "username" to user.username, "nickname" to user.nickname))
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequestDto: LoginRequestDto): ResponseEntity<TokenResponseDto> {
        val tokenResponse = authService.login(loginRequestDto)
        return ResponseEntity.ok(tokenResponse)
    }
}
