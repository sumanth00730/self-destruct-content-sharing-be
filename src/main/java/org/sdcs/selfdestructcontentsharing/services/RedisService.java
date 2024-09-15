package org.sdcs.selfdestructcontentsharing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    private static final String VIEW_PREFIX = "content:views:";

    // Increment view count in Redis
    public int incrementViewCount(Long contentId) {
        String key = VIEW_PREFIX + contentId;
        return Objects.requireNonNull(redisTemplate.opsForValue().increment(key)).intValue();
    }

    // Get view count from Redis
    public int getViewCount(Long contentId) {
        String key = VIEW_PREFIX + contentId;
        Integer count = redisTemplate.opsForValue().get(key);
        return count == null ? 0 : count;
    }

    // Delete view count key for a given content
    public void deleteViewCount(Long contentId) {
        String key = VIEW_PREFIX + contentId;
        redisTemplate.delete(key);
    }
}

