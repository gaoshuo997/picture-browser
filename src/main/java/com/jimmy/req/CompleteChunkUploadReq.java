package com.jimmy.req;

import lombok.Data;

@Data
public class CompleteChunkUploadReq {
    private String uploadId;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private Integer totalChunks;
    private Long userId;
    private String mediaType;
}