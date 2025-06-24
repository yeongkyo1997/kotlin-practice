package com.practice.board.controller

import com.practice.board.dto.PostRequestDto
import com.practice.board.dto.PostResponseDto
import com.practice.board.dto.PostSimpleResponseDto
import com.practice.board.service.PostService
import com.practice.board.service.PostLikeService // PostLikeService 임포트
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
    private val postLikeService: PostLikeService // PostLikeService 주입
) {

    @PostMapping
    fun createPost(@RequestBody postRequestDto: PostRequestDto): ResponseEntity<PostResponseDto> {
        val postResponse = postService.createPost(postRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse)
    }

    @GetMapping("/{postId}")
    fun getPostById(@PathVariable postId: Long): ResponseEntity<PostResponseDto> {
        // 조회수 증가 로직 호출
        postService.incrementViewCount(postId)
        val postResponse = postService.getPostById(postId)
        return ResponseEntity.ok(postResponse)
    }

    @GetMapping
    fun getAllPosts(
        @PageableDefault(size = 10, sort = ["createdAt,desc"]) pageable: Pageable,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<Page<PostSimpleResponseDto>> {
        val postsPage = postService.getAllPosts(pageable, category, keyword)
        return ResponseEntity.ok(postsPage)
    }

    @PutMapping("/{postId}")
    fun updatePost(
        @PathVariable postId: Long,
        @RequestBody postRequestDto: PostRequestDto
    ): ResponseEntity<PostResponseDto> {
        val updatedPost = postService.updatePost(postId, postRequestDto)
        return ResponseEntity.ok(updatedPost)
    }

    @DeleteMapping("/{postId}")
    fun deletePost(@PathVariable postId: Long): ResponseEntity<Void> {
        postService.deletePost(postId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{postId}/like")
    fun togglePostLike(@PathVariable postId: Long): ResponseEntity<Map<String, Any>> {
        val result = postLikeService.togglePostLike(postId)
        return ResponseEntity.ok(result)
    }

    // (선택적) 게시글 좋아요 상태 및 카운트 조회 API
    @GetMapping("/{postId}/like-state")
    fun getPostLikeState(@PathVariable postId: Long): ResponseEntity<Map<String, Any>> {
        val result = postLikeService.getLikeState(postId)
        return ResponseEntity.ok(result)
    }
}
