package org.example.business.implementation;

import org.example.business.dto.postDTO.PostRequestDto;
import org.example.business.dto.postDTO.PostResponseDto;
import org.example.persistance.PostRepository;
import org.example.persistance.entity.PostEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostServiceImpl Unit Tests")
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private PostEntity testPostEntity;
    private PostRequestDto testRequestDto;

    @BeforeEach
    void setUp() {
        testPostEntity = PostEntity.builder()
                .id(1L)
                .userId("user123")
                .title("Test Post Title")
                .content("Test post content")
                .createdAt(LocalDateTime.now())
                .build();

        testRequestDto = PostRequestDto.builder()
                .userId("user123")
                .title("Test Post Title")
                .content("Test post content")
                .build();
    }

    @Test
    @DisplayName("Happy Flow: Should create post successfully with valid data")
    void createPost_WithValidData_ShouldReturnPostResponseDto() {
        // Arrange
        when(postRepository.save(any(PostEntity.class))).thenReturn(testPostEntity);

        // Act
        PostResponseDto result = postService.createPost(testRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getTitle()).isEqualTo("Test Post Title");
        assertThat(result.getContent()).isEqualTo("Test post content");
        assertThat(result.getCreatedAt()).isNotNull();

        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @ParameterizedTest
    @MethodSource("provideValidPostData")
    @DisplayName("Happy Flow: Should create posts with different valid data combinations")
    void createPost_WithVariousValidData_ShouldSucceed(String userId, String title, String content) {
        // Arrange
        PostRequestDto requestDto = PostRequestDto.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .build();

        PostEntity savedEntity = PostEntity.builder()
                .id(1L)
                .userId(userId)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.save(any(PostEntity.class))).thenReturn(savedEntity);

        // Act
        PostResponseDto result = postService.createPost(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
    }

    static Stream<Arguments> provideValidPostData() {
        return Stream.of(
                Arguments.of("user1", "Title", "Short content"),
                Arguments.of("user2", "Medium Title Here", "This is a medium length content for testing purposes"),
                Arguments.of("user3", "Very Long Title With Multiple Words", "This is a very long content that simulates a real user post with multiple sentences and detailed information about their car issue or modification."),
                Arguments.of("user4", "Title with numbers 123", "Content with special chars: @#$%"),
                Arguments.of("user5", "Titlu în română", "Conținut cu caractere speciale: ăîâșț")
        );
    }

    @Test
    @DisplayName("Happy Flow: Should capture correct entity data when saving")
    void createPost_ShouldCaptureCorrectEntityData() {
        // Arrange
        ArgumentCaptor<PostEntity> entityCaptor = ArgumentCaptor.forClass(PostEntity.class);
        when(postRepository.save(any(PostEntity.class))).thenReturn(testPostEntity);

        // Act
        postService.createPost(testRequestDto);

        // Assert
        verify(postRepository).save(entityCaptor.capture());
        PostEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getUserId()).isEqualTo(testRequestDto.getUserId());
        assertThat(capturedEntity.getTitle()).isEqualTo(testRequestDto.getTitle());
        assertThat(capturedEntity.getContent()).isEqualTo(testRequestDto.getContent());
    }

    @Test
    @DisplayName("Happy Flow: Should return all posts successfully")
    void getAllPosts_WithExistingPosts_ShouldReturnListOfPosts() {
        // Arrange
        PostEntity post1 = PostEntity.builder()
                .id(1L)
                .userId("user1")
                .title("Post 1")
                .content("Content 1")
                .createdAt(LocalDateTime.now())
                .build();

        PostEntity post2 = PostEntity.builder()
                .id(2L)
                .userId("user2")
                .title("Post 2")
                .content("Content 2")
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        // Act
        List<PostResponseDto> result = postService.getAllPosts();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUserId()).isEqualTo("user1");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getUserId()).isEqualTo("user2");

        verify(postRepository, times(1)).findAll();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 50, 100})
    @DisplayName("Happy Flow: Should return correct number of posts")
    void getAllPosts_WithVariousNumberOfPosts_ShouldReturnCorrectSize(int postCount) {
        // Arrange
        List<PostEntity> posts = Stream.generate(() -> PostEntity.builder()
                        .id(1L)
                        .userId("user")
                        .title("Title")
                        .content("Content")
                        .createdAt(LocalDateTime.now())
                        .build())
                .limit(postCount)
                .toList();

        when(postRepository.findAll()).thenReturn(posts);

        // Act
        List<PostResponseDto> result = postService.getAllPosts();

        // Assert
        assertThat(result).hasSize(postCount);
    }

    @Test
    @DisplayName("Happy Flow: Should delete post successfully when user is owner")
    void deletePost_WhenUserIsOwner_ShouldDeleteSuccessfully() {
        // Arrange
        Long postId = 1L;
        String userId = "user123";

        when(postRepository.findById(postId)).thenReturn(Optional.of(testPostEntity));
        doNothing().when(postRepository).delete(any(PostEntity.class));

        // Act & Assert
        assertThatCode(() -> postService.deletePost(postId, userId))
                .doesNotThrowAnyException();

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(testPostEntity);
    }

    @ParameterizedTest
    @MethodSource("provideValidDeleteScenarios")
    @DisplayName("Happy Flow: Should delete posts in various valid scenarios")
    void deletePost_WithVariousValidScenarios_ShouldSucceed(Long postId, String userId, String postOwnerId) {
        // Arrange
        PostEntity post = PostEntity.builder()
                .id(postId)
                .userId(postOwnerId)
                .title("Title")
                .content("Content")
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).delete(any(PostEntity.class));

        // Act & Assert
        assertThatCode(() -> postService.deletePost(postId, userId))
                .doesNotThrowAnyException();
        verify(postRepository).delete(post);
    }

    static Stream<Arguments> provideValidDeleteScenarios() {
        return Stream.of(
                Arguments.of(1L, "user123", "user123"),
                Arguments.of(100L, "user456", "user456"),
                Arguments.of(5L, "550e8400-e29b-41d4-a716-446655440000", "550e8400-e29b-41d4-a716-446655440000"),
                Arguments.of(999L, "very-long-user-id-with-many-characters-123456789", "very-long-user-id-with-many-characters-123456789")
        );
    }

    @Test
    @DisplayName("Unhappy Flow: Should handle repository exception during create")
    void createPost_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        when(postRepository.save(any(PostEntity.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThatThrownBy(() -> postService.createPost(testRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
    }

    @Test
    @DisplayName("Unhappy Flow: Should handle repository exception during fetch")
    void getAllPosts_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        when(postRepository.findAll())
                .thenThrow(new RuntimeException("Database query failed"));

        // Act & Assert
        assertThatThrownBy(() -> postService.getAllPosts())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database query failed");
    }

    @Test
    @DisplayName("Unhappy Flow: Should throw exception when post not found")
    void deletePost_WhenPostNotFound_ShouldThrowException() {
        // Arrange
        Long postId = 999L;
        String userId = "user123";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.deletePost(postId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found with id: " + postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any(PostEntity.class));
    }

    @Test
    @DisplayName("Unhappy Flow: Should throw exception when user is not owner")
    void deletePost_WhenUserIsNotOwner_ShouldThrowException() {
        // Arrange
        Long postId = 1L;
        String userId = "user456"; // Different from post owner
        String postOwner = "user123";

        PostEntity post = PostEntity.builder()
                .id(postId)
                .userId(postOwner)
                .title("Title")
                .content("Content")
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        assertThatThrownBy(() -> postService.deletePost(postId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can only delete your own posts");

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any(PostEntity.class));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDeleteScenarios")
    @DisplayName("Unhappy Flow: Should fail deletion for various invalid scenarios")
    void deletePost_WithInvalidScenarios_ShouldThrowException(Long postId, String userId, String postOwnerId, String expectedMessage) {
        // Arrange
        PostEntity post = PostEntity.builder()
                .id(postId)
                .userId(postOwnerId)
                .title("Title")
                .content("Content")
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        assertThatThrownBy(() -> postService.deletePost(postId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(expectedMessage);

        verify(postRepository, never()).delete(any(PostEntity.class));
    }

    static Stream<Arguments> provideInvalidDeleteScenarios() {
        return Stream.of(
                Arguments.of(1L, "attacker", "owner123", "You can only delete your own posts"),
                Arguments.of(5L, "user456", "user123", "You can only delete your own posts"),
                Arguments.of(10L, "User123", "user123", "You can only delete your own posts"),
                Arguments.of(15L, "user123 ", "user123", "You can only delete your own posts")
        );
    }

    @Test
    @DisplayName("Unhappy Flow: Should handle repository exception during delete")
    void deletePost_WhenRepositoryThrowsExceptionOnDelete_ShouldPropagateException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPostEntity));
        doThrow(new RuntimeException("Database delete failed"))
                .when(postRepository).delete(any(PostEntity.class));

        // Act & Assert
        assertThatThrownBy(() -> postService.deletePost(1L, "user123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database delete failed");
    }

    @Test
    @DisplayName("Edge Case: Should return empty list when no posts exist")
    void getAllPosts_WhenNoPosts_ShouldReturnEmptyList() {
        // Arrange
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<PostResponseDto> result = postService.getAllPosts();

        // Assert
        assertThat(result).isEmpty();
        verify(postRepository, times(1)).findAll();
    }

    @ParameterizedTest
    @MethodSource("provideSpecialCharacterStrings")
    @DisplayName("Edge Case: Should handle special characters in post content")
    void createPost_WithSpecialCharacters_ShouldHandleCorrectly(String title, String content) {
        // Arrange
        PostRequestDto requestDto = PostRequestDto.builder()
                .userId("user123")
                .title(title)
                .content(content)
                .build();

        PostEntity savedEntity = PostEntity.builder()
                .id(1L)
                .userId("user123")
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.save(any(PostEntity.class))).thenReturn(savedEntity);

        // Act
        PostResponseDto result = postService.createPost(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
    }

    static Stream<Arguments> provideSpecialCharacterStrings() {
        return Stream.of(
                Arguments.of("Title\n\t\r", "Content with\nnewlines\tand\ttabs"),
                Arguments.of("<script>alert('xss')</script>", "Content with HTML"),
                Arguments.of("'; DROP TABLE posts; --", "SQL injection attempt"),
                Arguments.of("Title 🚗 🔧", "Content with emojis 😊 ✅")
        );
    }

    @Test
    @DisplayName("Edge Case: Should handle deletion of already deleted post")
    void deletePost_WhenPostAlreadyDeleted_ShouldThrowException() {
        // Arrange
        when(postRepository.findById(1L))
                .thenReturn(Optional.empty()); // Simulates post already deleted

        // Act & Assert
        assertThatThrownBy(() -> postService.deletePost(1L, "user123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Post not found with id: 1");
    }
}
