package com.jimmy.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 通用 JSON 工具类
 * 支持：对象/集合转 JSON、JSON 转对象/泛型集合、日期格式化、空值处理、异常封装
 */
public class JacksonUtils {

    /**
     * 全局 ObjectMapper 实例（线程安全）
     * 预配置：日期格式化、Java 8 时间类型支持、忽略未知字段、空值处理等
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 日期格式化常量
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    // 静态初始化：配置 ObjectMapper
    static {
        // 1. 注册 Java 8 时间模块（LocalDateTime/LocalDate/LocalTime）
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 序列化：Java 时间类型 → JSON 字符串
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        // 反序列化：JSON 字符串 → Java 时间类型
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        OBJECT_MAPPER.registerModule(javaTimeModule);

        // 2. 通用配置
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 忽略 JSON 中不存在的字段
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // 允许空对象序列化
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 日期不转时间戳，转字符串
    }

    // 私有构造方法：禁止实例化
    private JacksonUtils() {}

    // ==================== 基础方法：对象转 JSON ====================

    /**
     * 对象转 JSON 字符串（默认配置：包含空值、日期格式 yyyy-MM-dd HH:mm:ss）
     * @param obj 任意对象（实体类、集合、Map 等）
     * @return JSON 字符串
     * @throws RuntimeException 序列化失败时抛出运行时异常
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败：" + e.getMessage(), e);
        }
    }

    /**
     * 对象转 JSON 字符串（忽略空值）
     * @param obj 任意对象
     * @return JSON 字符串（空字段不会出现在结果中）
     */
    public static String toJsonIgnoreNull(Object obj) {
        try {
            return OBJECT_MAPPER.copy() // 复制实例，避免修改全局配置
                    .setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
                    .writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败（忽略空值）：" + e.getMessage(), e);
        }
    }

    // ==================== 基础方法：JSON 转对象 ====================

    /**
     * JSON 字符串转普通对象（非泛型）
     * @param jsonStr JSON 字符串
     * @param clazz 目标对象类型
     * @param <T> 泛型标记
     * @return 目标类型对象
     */
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败：" + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转泛型对象（如 List<User>、Map<String, User> 等）
     * @param jsonStr JSON 字符串
     * @param typeReference 泛型类型引用（示例：new TypeReference<List<User>>() {}）
     * @param <T> 泛型标记
     * @return 泛型对象
     */
    public static <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化（泛型）失败：" + e.getMessage(), e);
        }
    }

    // ==================== 扩展方法：获取自定义配置的 ObjectMapper ====================

    /**
     * 获取自定义配置的 ObjectMapper 实例（用于特殊场景）
     * @return ObjectMapper 实例
     */
    public static ObjectMapper getCustomObjectMapper() {
        return OBJECT_MAPPER.copy(); // 返回副本，避免修改全局配置
    }
}