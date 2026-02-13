package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.entity.Media;
import com.jimmy.req.MediaReq;
import com.jimmy.resp.MediaResp;
import org.springframework.web.multipart.MultipartFile;

public interface MediaUploadService {

    Media uploadImage(MultipartFile multipartFile);

    PaginatedApiResult<MediaResp> list(Integer page, Integer size, String type);

    MediaResp getDetail(Long id);

    Media saveMediaRecord(MediaReq mediaReq);

    PaginatedApiResult<MediaResp> publicList(Integer page, Integer size, String type);

    Media updateMediaRecord(MediaReq mediaReq);

    void deleteById(Long id);

    String getPresignedUrl(Long id);

}
