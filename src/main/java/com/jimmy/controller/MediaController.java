package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import com.jimmy.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MediaUploadService mediaUploadService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @PostMapping("/upload/image")
    public ApplicationResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile multipartFile){
        minioUtil.checkFile(multipartFile, MediaType.IMAGE);
        mediaUploadService.uploadImage(multipartFile);

        ApplicationResponseEntity<Map<String, Object>> result = new ApplicationResponseEntity<>();
        Map<String, Object> resultMap = new HashMap<>();
        result.setData(resultMap);
        return result;
    }

    @GetMapping("/list")
    public ApplicationResponseEntity<PaginatedApiResult<MediaResp>> list(
            @Pattern(regexp = "IMAGE|VIDEO", message = "媒体类型错误")
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size){
        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<MediaResp> pageResult = mediaUploadService.list(page, size, type);
        ApplicationResponseEntity<PaginatedApiResult<MediaResp>> result = new ApplicationResponseEntity<>();
        result.setData(pageResult);
        return result;
    }

    @GetMapping("/{id}")
    public ApplicationResponseEntity<MediaResp> getDetail(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        ApplicationResponseEntity<MediaResp> result = new ApplicationResponseEntity<>();
        MediaResp detail = mediaUploadService.getDetail(id);
        result.setData(detail);
        return result;
    }

    @GetMapping("/proxy/{id}")
    public ResponseEntity<Resource> proxyMedia(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) {
        try {
            return CompletableFuture.supplyAsync(() ->
                    mediaUploadService.getMediaResource(id, rangeHeader), taskExecutor).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取媒体资源被中断", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("获取媒体资源失败", e.getCause());
        }
    }

//    @DeleteMapping("/{id}")
//    public ApplicationResponseEntity delete(
//            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
//        return new ApplicationResponseEntity();
//    }


}
