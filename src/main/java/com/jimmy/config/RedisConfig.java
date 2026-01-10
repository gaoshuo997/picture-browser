package com.jimmy.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;

/**
 * Spring Boot 3.2.5 Redis配置类
 * 适配Lettuce客户端，解决序列化乱码，支持同步/响应式操作
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义RedisTemplate（通用对象操作）
     * 解决默认JDK序列化乱码问题，使用JSON序列化
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置Lettuce连接工厂（Spring Boot 3.x自动注入的是LettuceConnectionFactory）
        redisTemplate.setConnectionFactory(connectionFactory);

        // ========== 序列化配置 ==========
        // 1. Jackson序列化器（支持复杂对象，保留类型信息）
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 开启字段可见性，允许序列化所有字段（包括private）
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 开启类型信息存储（反序列化时能还原对象类型），Spring Boot 3.x推荐使用LaissezFaireSubTypeValidator
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        jacksonSerializer.serialize(objectMapper);

        // 2. String序列化器（Key/HashKey使用）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // ========== 应用序列化器 ==========
        // Key序列化
        redisTemplate.setKeySerializer(stringSerializer);
        // Hash Key序列化
        redisTemplate.setHashKeySerializer(stringSerializer);
        // Value
        redisTemplate.setValueSerializer(jacksonSerializer);
        // Hash Value序列化
        redisTemplate.setHashValueSerializer(jacksonSerializer);

        // 初始化RedisTemplate（必须调用，否则序列化配置不生效）
        redisTemplate.afterPropertiesSet();
        // 开启事务支持（按需开启，默认关闭）
        // redisTemplate.setEnableTransactionSupport(true);

        return redisTemplate;
    }

    /**
     * 优化的StringRedisTemplate（专用于字符串操作，性能更高）
     * Spring Boot默认提供，但此处显式声明以确保配置一致性
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        // StringRedisTemplate默认已使用String序列化器，无需额外配置
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }


    /**
     * 可选：自定义Lettuce连接工厂配置（覆盖默认配置，精细控制连接参数）
     * 如需定制Lettuce底层参数（如超时、集群配置），可启用此Bean
     */
    /*
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties) {
        // 1. 构建客户端配置
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(redisProperties.getTimeout()) // 命令超时
                .shutdownTimeout(redisProperties.getLettuce().getShutdownTimeout()) // 关闭超时
                .build();

        // 2. 构建连接配置
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(redisProperties.getHost());
        standaloneConfig.setPort(redisProperties.getPort());
        standaloneConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        standaloneConfig.setDatabase(redisProperties.getDatabase());

        // 3. 创建连接工厂
        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }
    */
}
