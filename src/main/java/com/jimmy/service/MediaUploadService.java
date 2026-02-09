package com.jimmy.service;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.entity.Media;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.req.MediaReq;
import com.jimmy.resp.MediaResp;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MediaUploadService {

    Media uploadImage(MultipartFile multipartFile);

    PaginatedApiResult<MediaResp> list(Integer page, Integer size, String type);

    MediaResp getDetail(Long id);

    Media saveMediaRecord(MediaReq mediaReq);

    PaginatedApiResult<MediaResp> publicList(Integer page, Integer size, String type);

    ResponseEntity<Resource> getMediaResource(Long id,String rangeHeader);

    Media updateMediaRecord(MediaReq mediaReq);

    void deleteById(Long id);

}
