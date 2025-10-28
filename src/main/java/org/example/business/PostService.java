package org.example.business;

import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;

import java.util.List;


public interface PostService {
    PostResponseDto createPost(PostRequestDto requestDto);
    List<PostResponseDto> getAllPosts();
    void deletePost(Long postId, String userId);
}