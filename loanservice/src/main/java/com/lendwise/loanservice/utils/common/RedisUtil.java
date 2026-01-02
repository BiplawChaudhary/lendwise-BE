package com.lendwise.loanservice.utils.common;

import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "lendwise.redis", havingValue = "true")
public class RedisUtil {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson;

    public void save(String key, Object value, String urn, boolean printLog) {
        ValueOperations<String, Object> ops = this.redisTemplate.opsForValue();
        String jsonValue = this.gson.toJson(value);
        ops.set(key, jsonValue);
        if (printLog) {
            this.printLog(urn, key, jsonValue);
        }

    }

    public void save(String key, Object value, long timeout, TimeUnit unit, String urn, boolean printLog) {
        log.info("[URN_{}] Redis save request for key:{}", urn, key);
        ValueOperations<String, Object> ops = this.redisTemplate.opsForValue();
        String jsonValue = this.gson.toJson(value);
        ops.set(key, jsonValue, timeout, unit);
        if (printLog) {
            this.printLog(urn, key, jsonValue);
        }

    }

    public String get(String key, String urn, boolean printLog) {
        ValueOperations<String, Object> ops = this.redisTemplate.opsForValue();
        String value = (String)ops.get(key);
        if (printLog) {
            this.printLog(urn, key, value);
        }

        return value;
    }

    private void printLog(String urn, String key, String value) {
        log.info("[URN_{}] Cache Key: {}, Cache Value: {}", new Object[]{urn, key, value});
    }

    @Generated
    public RedisUtil(final RedisTemplate<String, Object> redisTemplate, final Gson gson) {
        this.redisTemplate = redisTemplate;
        this.gson = gson;
    }
}