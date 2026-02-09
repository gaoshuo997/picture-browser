package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.core.BadRequestException;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.constant.UploadResultRecord;
import com.jimmy.entity.Media;
import com.jimmy.entity.SignUser;
import com.jimmy.entity.enums.BucketName;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.repository.MediaRepository;
import com.jimmy.req.MediaReq;
import com.jimmy.req.SignUserReq;
import com.jimmy.resp.MediaResp;
import com.jimmy.service.MediaUploadService;
import com.jimmy.utils.DateUtils;
import com.jimmy.utils.MinioUtil;
import com.jimmy.utils.UserUtils;
import io.minio.GetObjectArgs;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class MediaUploadServiceImpl implements MediaUploadService {

    @Resource
    private MediaRepository mediaRepository;
    @Resource
    private MinioUtil minioUtil;

    @Override
    public Media uploadImage(MultipartFile multipartFile) {
        Date now = new Date();
        UploadResultRecord resultRecord = minioUtil.uploadImage(multipartFile, BucketName.MO_JING.getMsg());
        String originalFilename = multipartFile.getOriginalFilename();
        try{
            Media media = new Media();
            media.setUserId(UserUtils.getUserId());
            media.setCreatedAt(now);
            media.setUpdatedAt(now);
            media.setUrl(resultRecord.accessUrl());
            if (originalFilename != null){
                media.setFileName(originalFilename.substring(0,originalFilename.lastIndexOf(".")));
            }
            media.setObjectName(resultRecord.fileName());
            media.setSize(multipartFile.getSize());
            media.setMediaType(MediaType.IMAGE);
            media.setBucketName(BucketName.MO_JING.getMsg());
            return mediaRepository.save(media);
        } catch (RuntimeException e) {
            minioUtil.removeObject(BucketName.MO_JING.getMsg(), resultRecord.fileName());
            throw new RuntimeException("保存图片元信息失败");
        }

    }

    @Override
    public PaginatedApiResult<MediaResp> list(Integer page, Integer size, String type) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "id"));
        MediaReq req = new MediaReq();
        if (type != null && !type.isEmpty()){
            req.setMediaType(type);
        }
        Specification<Media> mediaSpecification = buildSpecification(req);
        Page<Media> pageList = mediaRepository.findAll(mediaSpecification, pageable);

        List<MediaResp> resultList = new ArrayList<>(pageList.getSize());
        for (Media media : pageList){
            MediaResp resp = new MediaResp();
            BeanUtils.copyProperties(media,resp);
            resp.setType(media.getMediaType().toString());
            resp.setCreateAt(DateUtils.format(media.getCreatedAt(),DateUtils.DATETIME_FORMAT));
            resultList.add(resp);
        }
        return new PaginatedApiResult<>(pageable.getPageNumber(), pageable.getPageNumber(),
                resultList.size(),resultList);
    }

    @Override
    public MediaResp getDetail(Long id) {
        Media media = checkMediaExistOrNot(id);
        MediaResp resp = new MediaResp();
        BeanUtils.copyProperties(media, resp);
        resp.setType(media.getMediaType().toString());
        resp.setCreateAt(DateUtils.format(media.getCreatedAt(),DateUtils.DATETIME_FORMAT));
        return resp;
    }

    @Override
    public Media saveMediaRecord(@NotNull MediaReq mediaReq) {
        Date now = new Date();
        Media media = new Media();
        media.setUserId(mediaReq.getUserId());
        media.setCreatedAt(now);
        media.setUpdatedAt(now);
        media.setObjectName(mediaReq.getObjectName());

        String fileName = mediaReq.getFileName();
        if (fileName != null && fileName.contains(".")) {
            media.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        } else {
            media.setFileName(fileName);
        }
        media.setSize(mediaReq.getSize());
        media.setMediaType(MediaType.valueOf(mediaReq.getMediaType()));
        media.setUrl(minioUtil.getFileAccessUrl(mediaReq.getObjectName(), BucketName.MO_JING.getMsg()));
        return mediaRepository.save(media);
    }

    @Override
    public PaginatedApiResult<MediaResp> publicList(Integer page, Integer size, String type) {
        return this.list(page,size, type);
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> getMediaResource(Long id,String rangeHeader) {
        MediaResp detail = this.getDetail(id);
        if (!detail.getObjectName().isEmpty()){
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(BucketName.MO_JING.getMsg())
                    .object(detail.getObjectName())
                    .build();

            long fileSize = detail.getSize();
            if (fileSize <= 0) {
                return ResponseEntity.internalServerError().build();
            }

            try {
                InputStream stream = minioUtil.getObject(args);

                if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                    String rangeValue = rangeHeader.substring(6);
                    String[] ranges = rangeValue.split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileSize - 1;

                    long contentLength = end - start + 1;
                    long skipped = stream.skip(start);
                    while (skipped < start) {
                        skipped += stream.skip(start - skipped);
                    }
                    InputStreamResource resource = new InputStreamResource(stream);

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize)
                            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                            .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                            .body(resource);
                }

                InputStreamResource resource = new InputStreamResource(stream);
                return ResponseEntity.ok()
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                        .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                        .body(resource);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
        }else {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public Media updateMediaRecord(@NotNull MediaReq mediaReq) {
        Media media = checkMediaExistOrNot(mediaReq.getId());
        media.setThumbnail(mediaReq.getThumbnail());
        media.setUpdatedAt(new Date());
        return mediaRepository.save(media);
    }

    @Override
    public void deleteById(Long id) {
        Media media = checkMediaExistOrNot(id);
        minioUtil.removeObject(media);
    }

    /**
     * 构建查询条件
     * @param req 请求查询条件
     * @return 返回查询条件
     */
    private Specification<Media> buildSpecification(MediaReq req){
        return (root, query, cb) -> {
            // 存储查询条件的集合
            List<Predicate> predicates = new ArrayList<>();
            if (req.getMediaType() != null && !req.getMediaType().isEmpty()){
                predicates.add(cb.equal(root.get("mediaType"), MediaType.valueOf(req.getMediaType())));
            }
            if (UserUtils.getUserId() != null){
                predicates.add(cb.equal(root.get("userId"), UserUtils.getUserId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

    /**
     * 检查这个媒体信息是否存在
     * @param id 媒体ID
     * @return 媒体
     */
    @NotNull
    private Media checkMediaExistOrNot(Long id){
        Optional<Media> mediaOptional = mediaRepository.findById(id);
        if (mediaOptional.isEmpty()){
            throw new BadRequestException(BadReqExceptionMsg.MEDIA_IS_NOT_EXIST.getCode(),
                    BadReqExceptionMsg.MEDIA_IS_NOT_EXIST.getMessage(), BadReqExceptionMsg.MEDIA_IS_NOT_EXIST.getMessage());
        }
        return mediaOptional.get();
    }
}
