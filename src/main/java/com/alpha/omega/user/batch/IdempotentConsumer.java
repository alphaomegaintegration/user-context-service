package com.alpha.omega.user.batch;

import com.alpha.omega.user.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class IdempotentConsumer {
    private static final Logger logger = LoggerFactory.getLogger(IdempotentConsumer.class);


    StringRedisTemplate redisTemplate;
    String entityKeysName;

    public IdempotentConsumer(StringRedisTemplate redisTemplate, String entityKeysName) {
        this.redisTemplate = redisTemplate;
        this.entityKeysName = entityKeysName;
    }

    public Long consume(String itemId) {
        return redisTemplate.opsForSet().add(entityKeysName, itemId);
    }

    public boolean isProcessed(String itemId) {
        return redisTemplate.opsForSet().isMember(entityKeysName, itemId);
    }

}
