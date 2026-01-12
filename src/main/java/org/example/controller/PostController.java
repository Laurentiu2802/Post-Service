package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.business.PostService;
import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@AllArgsConstructor
@Validated  // <-- ADD THIS FOR @RequestParam VALIDATION
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestHeader(value = "X-User-Id") String userId,
            @Valid @RequestBody PostRequestDto requestDto) {

        log.info("Create post request from user: {}", userId);

        requestDto.setUserId(userId);
        PostResponseDto response = postService.createPost(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0") @Min(0) int page,  // <-- ADD @Min
            @RequestParam(defaultValue = "100") @Min(1) @Max(500) int size  // <-- ADD @Min @Max
    ) {
        log.info("Get all posts request - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostResponseDto> postsPage = postService.getAllPosts(pageable);

        return ResponseEntity.ok(postsPage.getContent());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @RequestHeader(value = "X-User-Id") String userId,
            @PathVariable Long postId) {

        log.info("Delete post {} request from user: {}", postId, userId);

        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}