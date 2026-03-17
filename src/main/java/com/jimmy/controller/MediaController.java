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

/**
 * 媒体接口管理
 */
@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MediaUploadService mediaUploadService;

    /**
     * 上传图片接口
     * @param multipartFile 图片数据
     * @return 返回媒体信息
     */
    @PostMapping("/upload/image")
    public Result<MediaResp> uploadImage(@RequestParam("file") MultipartFile multipartFile){
        minioUtil.checkFile(multipartFile, MediaType.IMAGE);
        MediaResp resp = mediaUploadService.uploadImage(multipartFile);
        return Result.success(resp);
    }

    /**
     * 分页查询媒体列表
     * @param type 媒体类型（IMAGE：图片｜VIDEO：视频）
     * @param page 页码
     * @param size 每页多少条
     * @return 返回分页列表
     */
    @GetMapping("/list")
    public Result<PaginatedApiResult<MediaResp>> list(
            @Pattern(regexp = "IMAGE|VIDEO", message = "媒体类型错误")
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "fileName", required = false) String fileName){
        page = page >= 1 ? page : 1;
        size = size >= 0 ? size : 10;
        PaginatedApiResult<MediaResp> pageResult = mediaUploadService.list(page, size, type, fileName);
        return Result.success(pageResult);
    }

    /**
     * 获取媒体详情
     * @param id 媒体ID
     * @return 媒体详情
     */
    @GetMapping("/{id}")
    public Result<MediaResp> getDetail(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        MediaResp detail = mediaUploadService.getDetail(id);
        return Result.success(detail);
    }

    /**
     * 获取媒体的预签名地址（用于前端直接展示，不经过后端）
     * @param id 媒体ID
     * @return 返回预签名地址
     */
    @GetMapping("/presignedUrl/{id}")
    public Result<String> getPreSignedUrl(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        String presignedUrl = mediaUploadService.getPresignedUrl(id);
        return Result.success(presignedUrl);
    }

    /**
     * 删除文件
     * @param id 文件ID
     * @return 空
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        mediaUploadService.deleteById(id);
        return Result.success();
    }


}
