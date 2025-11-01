package org.example.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.configuration.RabbitMQConfig;
import org.example.persistance.PostRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class UserDeletedListener {

    private final PostRepository postRepository;

    @RabbitListener(queues = RabbitMQConfig.POST_QUEUE)
    @Transactional
    public void handleUserDeleted(String userId) {
        log.info("Received user.deleted event for userId: {}", userId);

        try {
            int deletedCount = postRepository.deleteByUserId(userId);
            log.info("Deleted {} posts for user: {}", deletedCount, userId);
        } catch (Exception e) {
            log.error("Error deleting posts for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }
}