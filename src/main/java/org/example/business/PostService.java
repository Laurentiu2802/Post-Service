package org.example.business;

import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;

public interface PostService {
    PostResponseDto createPost(PostRequestDto requestDto);
}