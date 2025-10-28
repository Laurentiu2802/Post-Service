package org.example.business.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.business.PostService;
import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;
import org.example.persistance.PostRepository;
import org.example.persistance.entity.PostEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        log.info("Creating post for user: {}", requestDto.getUserId());

        PostEntity post = PostEntity.builder()
                .userId(requestDto.getUserId())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        post = postRepository.save(post);
        log.info("Post created successfully with ID: {}", post.getId());

        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}