package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.result.Result;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/public")
public class PublicMediaController {

    @Autowired
    private MediaUploadService mediaUploadService;

    @GetMapping("/media/list")
    public Result<PaginatedApiResult<MediaResp>> list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer size){
        page = page >= 1 ? page : 1;

        // 公共接口只能获取最大20条图片
        size = 0 <= size && size <= 20 ? size : 20;
        PaginatedApiResult<MediaResp> pageResult = mediaUploadService.publicList(page, size, MediaType.IMAGE.toString());
        return Result.success(pageResult);
    }

    @GetMapping("/media/{id}")
    public Result<MediaResp> getDetail(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        MediaResp detail = mediaUploadService.getDetail(id);
        return Result.success(detail);
    }

    @GetMapping("/media/presignedUrl/{id}")
    public Result<String> getPreSignedUrl(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        String presignedUrl = mediaUploadService.getPresignedUrl(id);
        return Result.success(presignedUrl);
    }
}
