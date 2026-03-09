package com.jimmy.controller;

import com.jimmy.common.result.Result;
import com.jimmy.entity.Media;
import com.jimmy.entity.enums.BucketName;
import com.jimmy.req.CompleteChunkUploadReq;
import com.jimmy.req.MediaReq;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import com.jimmy.utils.MinioUtil;
import com.jimmy.utils.UserUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * 分片上传视频
 */
@RestController
@RequestMapping("/chunk-upload")
public class ChunkUploadController {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MediaUploadService mediaUploadService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    /**
     * 初始化上传
     */
    @PostMapping("/init")
    public Result<InitUploadResp> InitUpload(@RequestBody InitUploadReq req) {
        String uploadId = UUID.randomUUID().toString();
        minioUtil.createBucketIfNotExists(BucketName.MO_JING.getMsg());
        return Result.success(new InitUploadResp(uploadId, BucketName.MO_JING.getMsg()));
    }

    /**
     * 上传分片
     */
    @PostMapping("/chunk")
    public Result<String> uploadChunk(
            @RequestParam("file") MultipartFile chunk,
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkNumber") Integer chunkNumber,
            @RequestParam("bucketName") String bucketName) throws IOException {
        try {
            // 将分片数据读取到字节数组，避免 InputStream 在异步任务中被关闭
            byte[] chunkData = chunk.getBytes();
            long chunkSize = chunk.getSize();

            String chunkName = CompletableFuture.supplyAsync(() ->
                    minioUtil.uploadChunk(bucketName, uploadId, chunkNumber,
                            new ByteArrayInputStream(chunkData), chunkSize), taskExecutor).get();
            return Result.success(chunkName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("上传分片被中断", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("上传分片失败", e.getCause());
        }
    }

    /**
     * 完成上传
     */
    @PostMapping("/complete")
    public Result<Map<String, Object>> completeUpload(@RequestBody CompleteChunkUploadReq req) {
        String objectName = UUID.randomUUID() + req.getFileName();
        minioUtil.completeChunkUpload(req.getBucketName(), req.getUploadId(),
                req.getTotalChunks(), objectName, req.getContentType());

        MediaReq saveMedia = new MediaReq();
        saveMedia.setObjectName(objectName);
        saveMedia.setMediaType(req.getMediaType());
        saveMedia.setFileName(req.getFileName());
        saveMedia.setSize(req.getFileSize());
        saveMedia.setUserId(UserUtils.getUserId());
        saveMedia.setBucketName(req.getBucketName());
        Media media = mediaUploadService.saveMediaRecord(saveMedia);

        Map<String, Object> result = new HashMap<>(2);
        result.put("objectName", objectName);
        result.put("id", media.getId());
        result.put("bucketName", media.getBucketName());
        return Result.success(result);
    }

    @PostMapping("/coverFile")
    public Result<Map<String, Object>> coverFile(
            @RequestParam("coverFile") MultipartFile coverFile,
            @RequestParam("videoId") Long videoId){
        // 上传视频封面
        MediaResp coverImage = mediaUploadService.uploadImage(coverFile);
        MediaResp videoDetail = mediaUploadService.getDetail(videoId);

        // 将上传的视频封面与视频关联起来
        videoDetail.setThumbnail(coverImage.getId());
        MediaReq mediaReq = new MediaReq();
        BeanUtils.copyProperties(videoDetail,mediaReq);
        // 更新视频信息
        Media newVideo = mediaUploadService.updateMediaRecord(mediaReq);

        Map<String, Object> result = new HashMap<>(2);
        result.put("thumbnailFileName", coverImage.getFileName());
        result.put("thumbnailId", newVideo.getThumbnail());
        return Result.success(result);
    }

    /**
     * 取消上传
     */
    @PostMapping("/abort")
    public Result<?> abortUpload(@RequestBody AbortUploadReq req) {
        minioUtil.abortChunkUpload(req.getBucketName(), req.getUploadId(), req.getUploadedChunks());
        return Result.success();
    }

    @Data
    public static class InitUploadReq {
        private String fileName;
        private Long fileSize;
        private Integer totalChunks;
        private String contentType;
    }

    @Data
    public static class InitUploadResp {
        private String uploadId;
        private String bucketName;

        public InitUploadResp(String uploadId, String bucketName) {
            this.uploadId = uploadId;
            this.bucketName = bucketName;
        }
    }

    @Data
    public static class AbortUploadReq {
        private String uploadId;
        private Integer uploadedChunks;
        private String bucketName;
    }
}