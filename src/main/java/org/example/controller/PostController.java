package org.example.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.business.PostService;
import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/posts")
@AllArgsConstructor
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
}