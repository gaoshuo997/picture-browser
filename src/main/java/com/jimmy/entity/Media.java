package com.jimmy.entity;

import com.jimmy.entity.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "thumbnail")
    private Long thumbnail;

    @Column(name = "size")
    private Long size;

    // 视频时长
    @Column(name = "duration")
    private Integer duration;

    // 标签
    @Column(name = "tags", length = 64)
    private String tags;

    // 喜欢数量
    @Column(name = "likes")
    private Integer likes;

    // 是否喜欢
    @Column(name = "liked")
    private Boolean liked;

    // 是否最喜欢
    @Column(name = "favorite")
    private Boolean favorite;

    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

}
