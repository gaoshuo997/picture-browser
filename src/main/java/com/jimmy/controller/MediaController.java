package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.Result;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import com.jimmy.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MediaUploadService mediaUploadService;

    @PostMapping("/upload/image")
    public Result<MediaResp> uploadImage(@RequestParam("file") MultipartFile multipartFile){
        minioUtil.checkFile(multipartFile, MediaType.IMAGE);
        MediaResp resp = mediaUploadService.uploadImage(multipartFile);
        return Result.success(resp);
    }

    @GetMapping("/list")
    public Result<PaginatedApiResult<MediaResp>> list(
            @Pattern(regexp = "IMAGE|VIDEO", message = "媒体类型错误")
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size){
        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<MediaResp> pageResult = mediaUploadService.list(page, size, type);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<MediaResp> getDetail(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        MediaResp detail = mediaUploadService.getDetail(id);
        return Result.success(detail);
    }

    @GetMapping("/presignedUrl/{id}")
    public Result<String> getPreSignedUrl(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        String presignedUrl = mediaUploadService.getPresignedUrl(id);
        return Result.success(presignedUrl);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        mediaUploadService.deleteById(id);
        return Result.success();
    }


}
