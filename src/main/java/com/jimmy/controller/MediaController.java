package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import com.jimmy.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private MediaUploadService mediaUploadService;

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

    @GetMapping("/presignedUrl/{id}")
    public ApplicationResponseEntity<String> getPreSignedUrl(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        String presignedUrl = mediaUploadService.getPresignedUrl(id);
        ApplicationResponseEntity<String> result = new ApplicationResponseEntity<>();
        result.setData(presignedUrl);
        return result;
    }

    @DeleteMapping("/{id}")
    public ApplicationResponseEntity<Map<String, Object>> delete(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        mediaUploadService.deleteById(id);
        ApplicationResponseEntity<Map<String, Object>> result = new ApplicationResponseEntity<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("message","删除成功");
        result.setData(resultMap);
        return result;
    }


}
