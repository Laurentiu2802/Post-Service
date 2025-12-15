package org.example.business;

import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostService {
    PostResponseDto createPost(PostRequestDto requestDto);
    Page<PostResponseDto> getAllPosts(Pageable pageable);


    void deletePost(Long postId, String userId);
}