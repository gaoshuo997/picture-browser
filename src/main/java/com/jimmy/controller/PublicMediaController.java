package com.jimmy.controller;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.web.ApplicationResponseEntity;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/public")
public class PublicMediaController {

    @Autowired
    private MediaUploadService mediaUploadService;

    @GetMapping("/media/list")
    public ApplicationResponseEntity<PaginatedApiResult<MediaResp>> list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer size){
        page = page >= 1 ? page : 1;

        // 公共接口只能获取最大20条图片
        size = 0 <= size && size <= 20 ? size : 20;
        PaginatedApiResult<MediaResp> pageResult = mediaUploadService.publicList(page, size, MediaType.IMAGE.toString());
        ApplicationResponseEntity<PaginatedApiResult<MediaResp>> result = new ApplicationResponseEntity<>();
        result.setData(pageResult);
        return result;
    }

    @GetMapping("/media/{id}")
    public ApplicationResponseEntity<MediaResp> getDetail(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id){
        ApplicationResponseEntity<MediaResp> result = new ApplicationResponseEntity<>();
        MediaResp detail = mediaUploadService.getDetail(id);
        result.setData(detail);
        return result;
    }

    /**
     * todo 这里要做混淆处理，由于ID策略是自增的其余的资源很容易被猜到ID
     * @param id 媒体资源ID
     * @param rangeHeader 请求头
     * @return 返回数据流
     */
    @GetMapping("/media/proxy/{id}")
    public ResponseEntity<Resource> proxyMedia(
            @PathVariable @NotNull(message = "媒体的id不能为空") Long id,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) {
        return mediaUploadService.getMediaResource(id, rangeHeader);
    }
}
