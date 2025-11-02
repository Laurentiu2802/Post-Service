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

import java.util.List;
import java.util.stream.Collectors;

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

        return mapToResponseDto(post);
    }

    @Override
    public List<PostResponseDto> getAllPosts() {
        log.info("Fetching all posts");

        return postRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String userId) {
        log.info("Deleting post {} by user {}", postId, userId);

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Check if the user owns the post
        if (!post.getUserId().equals(userId)) {
            log.error("User {} attempted to delete post {} owned by {}", userId, postId, post.getUserId());
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
        log.info("Post {} deleted successfully", postId);
    }

    private PostResponseDto mapToResponseDto(PostEntity post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}