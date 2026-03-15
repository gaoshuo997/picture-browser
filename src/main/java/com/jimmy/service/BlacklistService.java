package com.jimmy.service;

import com.jimmy.entity.enums.RedisKeyName;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlacklistService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将用户加入黑名单，实现踢下线的操作
     * @param token 用户token
     * @param expireTime token过期时间
     */
    public void addToBlacklist(String token, long expireTime){
        stringRedisTemplate.opsForValue().set(RedisKeyName.BLACKLIST_PREFIX.getName() + token,
                "blocked",expireTime, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token){
        return stringRedisTemplate.hasKey(RedisKeyName.BLACKLIST_PREFIX.getName() + token);
    }

}
