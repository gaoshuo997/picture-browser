package com.jimmy.utils;

import com.jimmy.common.core.BadRequestException;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.constant.UploadResultRecord;
import com.jimmy.entity.enums.MediaType;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * MinIO 工具类：封装文件上传、获取访问地址方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.read-path}")
    private String readPath;

    @Value("${minio.expire-time}")
    private Integer expireTime;

    private final List<String> imageTypeList = new ArrayList<>(Arrays.asList("jpeg","png","gif","webpg"));

    private final List<String> videoTypeList = new ArrayList<>(Arrays.asList("mp4","avi","mov"));
    /**
     * 图片上传核心方法
     * @param multipartFile 前端上传的文件（Spring Web 封装的MultipartFile）
     * @return 图片的**完整访问地址**（可直接用于前端展示）
     */
    public UploadResultRecord uploadImage(@NotNull MultipartFile multipartFile, String bucketName) {
        // 1. 校验文件是否为空
        if (multipartFile.isEmpty()) {
            throw new BadRequestException(BadReqExceptionMsg.UPLOAD_IMG_IS_NULL.getCode(),BadReqExceptionMsg.UPLOAD_IMG_IS_NULL.getMessage(),
                    BadReqExceptionMsg.UPLOAD_IMG_IS_NULL.getMessage());
        }
        if (bucketName.isEmpty()){
            throw new BadRequestException(BadReqExceptionMsg.BUCKET_NAME_IS_NULL.getCode(),BadReqExceptionMsg.BUCKET_NAME_IS_NULL.getMessage(),
                    BadReqExceptionMsg.BUCKET_NAME_IS_NULL.getMessage());
        }

        createBucketIfNotExists(bucketName);
        try {
            // 2. 获取文件原始名称和输入流
            String originalFilename = multipartFile.getOriginalFilename();
            assert originalFilename != null;
            // 3. 生成唯一文件名（避免同名文件覆盖，UUID + 原文件后缀）
            String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            InputStream inputStream = multipartFile.getInputStream();
            // 4. 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 存储桶名
                            .object(fileName)   // 存储到MinIO的文件名
                            .stream(inputStream, multipartFile.getSize(), -1) // 文件流 + 大小（-1表示自动检测）
                            .contentType(multipartFile.getContentType()) // 文件类型（如image/jpeg、image/png）
                            .build()
            );

            // 5. 生成并返回文件完整访问地址
            String fileAccessUrl = getFileAccessUrl(fileName, bucketName);
            log.info("图片上传成功，文件名：{}，访问地址：{}", fileName, fileAccessUrl);
            return new UploadResultRecord(fileAccessUrl, fileName);

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("图片上传失败，异常信息：{}", e.getMessage(), e);
            throw new RuntimeException("图片上传MinIO失败：" + e.getMessage());
        }
    }

    /**
     * 根据MinIO中的文件名，生成完整的文件访问地址
     * @param fileName MinIO中存储的唯一文件名
     * @return 完整访问地址（readPath + 桶名 + 文件名）
     */
    public String getFileAccessUrl(String fileName, String bucketName) {
        // 拼接规则：访问基础路径 / 桶名 / 文件名
        return String.format("%s/%s/%s", readPath, bucketName, fileName);
    }

    /**
     * 桶是否存在,不存在则创建
     * @param bucketName 桶名称
     */
    public void createBucketIfNotExists(String bucketName){
        if (bucketName.isEmpty()){
            throw new BadRequestException(BadReqExceptionMsg.BUCKET_NAME_IS_NULL.getCode(),BadReqExceptionMsg.BUCKET_NAME_IS_NULL.getMessage(),
                    BadReqExceptionMsg.BUCKET_NAME_IS_NULL.getMessage());
        }
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("创建minio桶失败！");
        }
    }

    /**
     * 检查文件类型是否正确
     * @param file 文件
     * @param type 文件类型（图片｜视频）
     */
    public void checkFile(@NotNull MultipartFile file, @NotNull MediaType type){
        String suffixName = Objects.requireNonNull(file.getOriginalFilename()).
                substring(file.getOriginalFilename().lastIndexOf("."));
        log.info("文件后缀名:{}",suffixName);
        if (type.equals(MediaType.IMAGE)){
            if (imageTypeList.contains(suffixName)){
                throw new BadRequestException(BadReqExceptionMsg.UPLOAD_IMG_TYPE_ERROR.getCode(),
                        BadReqExceptionMsg.UPLOAD_IMG_TYPE_ERROR.getMessage(), BadReqExceptionMsg.UPLOAD_IMG_TYPE_ERROR.getMessage());

            }
        }
        if (type.equals(MediaType.VIDEO)){
            if (videoTypeList.contains(suffixName)){
                throw new BadRequestException(BadReqExceptionMsg.UPLOAD_VIDEO_TYPE_ERROR.getCode(),
                        BadReqExceptionMsg.UPLOAD_VIDEO_TYPE_ERROR.getMessage(), BadReqExceptionMsg.UPLOAD_VIDEO_TYPE_ERROR.getMessage());

            }
        }
    }

    /**
     * 删除文件对像
     * @param bucketName 桶名称
     * @param objectName 文件对像名称
     */
    public void removeObject(String bucketName, String objectName){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception minioDelE) {
            // 记录Minio删除失败的日志，避免吞掉原始异常
            log.error("Minio文件回滚删除失败，对象名：{}，原因：{}", objectName, minioDelE.getMessage(), minioDelE);
            throw new BadRequestException(BadReqExceptionMsg.REMOVE_OBJECT_ERROR.getCode(), BadReqExceptionMsg.REMOVE_OBJECT_ERROR.getMessage(),
                    BadReqExceptionMsg.REMOVE_OBJECT_ERROR.getMessage());
        }
    }

    public InputStream getObject(GetObjectArgs args){
        try {
            return minioClient.getObject(args);
        }catch (Exception e){
            throw new RuntimeException("获取媒体失败");
        }
    }

    /**
     * 上传分片（临时存储）
     * @param bucketName 桶名称
     * @param uploadId 上传ID
     * @param chunkNumber 分片序号（从1开始）
     * @param chunkStream 分片数据流
     * @param chunkSize 分片大小
     * @return 分片对象名称
     */
    public String uploadChunk(String bucketName, String uploadId, int chunkNumber,
                             InputStream chunkStream, long chunkSize) {
        try {
            String chunkObjectName = "temp/" + uploadId + "/" + chunkNumber;
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(chunkObjectName)
                            .stream(chunkStream, chunkSize, -1)
                            .contentType("application/octet-stream")
                            .build()
            );
            return chunkObjectName;
        } catch (Exception e) {
            log.error("上传分片失败，uploadId：{}，分片序号：{}", uploadId, chunkNumber, e);
            throw new RuntimeException("上传分片失败：" + e.getMessage());
        }
    }

    /**
     * 完成分片上传（合并所有分片）
     * @param bucketName 桶名称
     * @param uploadId 上传ID
     * @param totalChunks 总分片数
     * @param finalObjectName 最终对象名称
     * @param contentType 内容类型
     */
    @SuppressWarnings("unused")
    public void completeChunkUpload(String bucketName, String uploadId, int totalChunks,
                                   String finalObjectName, String contentType) {
        try {
            List<ComposeSource> sources = new ArrayList<>();
            for (int i = 1; i <= totalChunks; i++) {
                String chunkObjectName = "temp/" + uploadId + "/" + i;
                sources.add(
                        ComposeSource.builder()
                                .bucket(bucketName)
                                .object(chunkObjectName)
                                .build()
                );
            }

            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(finalObjectName)
                            .sources(sources)
                            .build()
            );

            // 删除临时分片
            abortChunkUpload(bucketName, uploadId, totalChunks);

        } catch (Exception e) {
            log.error("完成分片上传失败，uploadId：{}", uploadId, e);
            throw new RuntimeException("完成分片上传失败：" + e.getMessage());
        }
    }

    /**
     * 取消分片上传（删除临时分片）
     * @param bucketName 桶名称
     * @param uploadId 上传ID
     * @param totalChunks 已上传的分片数
     */
    public void abortChunkUpload(String bucketName, String uploadId, int totalChunks) {
        for (int i = 1; i <= totalChunks; i++) {
            String chunkObjectName = "temp/" + uploadId + "/" + i;
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(chunkObjectName)
                                .build()
                );
            } catch (Exception e) {
                log.warn("删除临时分片失败：{}", chunkObjectName, e);
                throw new RuntimeException("删除临时分片失败",e);
            }
        }
    }

    public String getPresignedObjectUrl(String bucketName, String objectName){
        try{
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expireTime)
                    .build());
        }catch (Exception e){
            throw new BadRequestException(BadReqExceptionMsg.PRE_SIGNED_ERROR.getCode(), BadReqExceptionMsg.PRE_SIGNED_ERROR.getMessage(),
                    BadReqExceptionMsg.PRE_SIGNED_ERROR.getMessage());
        }
    }
}
