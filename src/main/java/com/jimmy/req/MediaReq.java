package com.jimmy.req;

import lombok.Data;

@Data
public class MediaReq {
    private Long id;

    private Long userId;

    private String mediaType;

    private String fileName;

    private String objectName;

    private String url;

    private Long thumbnail;

    private Long size;

    // 视频时长
    private Integer duration;

    // 标签
    private String tags;

    // 喜欢数量
    private Integer likes;

    // 是否喜欢
    private Boolean liked;

    // 是否最喜欢
    private Boolean favorite;

    // 桶名称
    private String bucketName;
}
