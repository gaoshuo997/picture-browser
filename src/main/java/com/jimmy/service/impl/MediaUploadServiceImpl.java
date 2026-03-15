package com.jimmy.service.impl;

import com.jimmy.common.PaginatedApiResult;
import com.jimmy.common.exception.BadReqExceptionMsg;
import com.jimmy.common.result.BusinessException;
import com.jimmy.constant.PredicateFieldName;
import com.jimmy.constant.UploadResultRecord;
import com.jimmy.entity.Media;
import com.jimmy.entity.enums.BucketName;
import com.jimmy.entity.enums.MediaType;
import com.jimmy.entity.enums.RedisKeyName;
import com.jimmy.repository.MediaRepository;
import com.jimmy.req.MediaReq;
import com.jimmy.resp.MediaResp;
import com.jimmy.security.SecurityUtils;
import com.jimmy.service.MediaUploadService;
import com.jimmy.utils.DateUtils;
import com.jimmy.utils.MinioUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.ExpireChanges;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class MediaUploadServiceImpl implements MediaUploadService {

    @Resource
    private MediaRepository mediaRepository;
    @Resource
    private MinioUtil minioUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public MediaResp uploadImage(MultipartFile multipartFile) {
        LocalDateTime now = LocalDateTime.now();
        UploadResultRecord resultRecord = minioUtil.uploadImage(multipartFile, BucketName.MO_JING.getMsg());
        String originalFilename = multipartFile.getOriginalFilename();
        try{
            Media media = new Media();
            media.setUserId(SecurityUtils.getCurrentUserId());
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
            Media save = mediaRepository.save(media);

            MediaResp resp = new MediaResp();
            BeanUtils.copyProperties(save, resp);
            resp.setCreateAt(DateUtils.format(save.getCreatedAt(), DateUtils.DATETIME_FORMAT));
            return resp;
        } catch (RuntimeException e) {
            minioUtil.removeObject(BucketName.MO_JING.getMsg(), resultRecord.fileName());
            throw new RuntimeException("保存图片元信息失败");
        }

    }

    @Override
    public PaginatedApiResult<MediaResp> list(Integer page, Integer size, String type) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, PredicateFieldName.ID.getName()));
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
        return new PaginatedApiResult<>(pageable.getPageNumber(),pageable.getPageSize(),
                resultList.size(),pageList.getTotalElements(),
                resultList,pageList.getTotalPages());
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
        LocalDateTime now = LocalDateTime.now();
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
        media.setBucketName(mediaReq.getBucketName());
        media.setMediaType(MediaType.valueOf(mediaReq.getMediaType()));
        media.setUrl(minioUtil.getFileAccessUrl(mediaReq.getObjectName(), mediaReq.getBucketName()));
        return mediaRepository.save(media);
    }

    @Override
    public PaginatedApiResult<MediaResp> publicList(Integer page, Integer size, String type) {
        return this.list(page,size, type);
    }

    @Override
    public Media updateMediaRecord(@NotNull MediaReq mediaReq) {
        Media media = checkMediaExistOrNot(mediaReq.getId());
        media.setThumbnail(mediaReq.getThumbnail());
        media.setUpdatedAt(LocalDateTime.now());
        return mediaRepository.save(media);
    }

    @Override
    public void deleteById(Long id) {
        Media media = checkMediaExistOrNot(id);
        minioUtil.removeObject(media.getBucketName(), media.getObjectName());
        // 如果是媒体资源，有封面的删除封面
        if (media.getMediaType().equals(MediaType.VIDEO)){
            Optional<Media> byId = mediaRepository.findById(media.getThumbnail());
            byId.ifPresent(value -> mediaRepository.deleteById(value.getId()));
        }
        mediaRepository.deleteById(id);
    }

    @Override
    public String getPresignedUrl(Long id) {
        String redisKey = RedisKeyName.MEDIA_PRE_SiGN_URL.getName();
        String idStr = String.valueOf(id);
        Boolean hasKey = stringRedisTemplate.opsForHash().hasKey(redisKey, idStr);
        if (hasKey){
            return Objects.requireNonNull(stringRedisTemplate.opsForHash()
                    .get(RedisKeyName.MEDIA_PRE_SiGN_URL.getName(), idStr)).toString();
        }
        Media media = checkMediaExistOrNot(id);
        try {
            String presignedObjectUrl = minioUtil.getPresignedObjectUrl(media.getBucketName(), media.getObjectName());
            media.setUrl(presignedObjectUrl);
            mediaRepository.save(media);

            stringRedisTemplate.opsForHash().put(redisKey, idStr, presignedObjectUrl);
            ExpireChanges<Object> expire = stringRedisTemplate.opsForHash().expire(redisKey, Duration.ofSeconds(minioUtil.getExpireTime()),
                    Collections.singletonList(idStr));
//        Boolean expireResult = stringRedisTemplate.expire(redisKey, Duration.ofHours(12));
            if (expire == null || !expire.allChanged()) {
                log.warn("Set Redis TTL failed for key: {}, 媒体文件ID: {}", redisKey, idStr);
                stringRedisTemplate.opsForHash().delete(redisKey, idStr);
            }
            return presignedObjectUrl;
        }catch (Exception e){
            throw new BusinessException(BadReqExceptionMsg.PRE_SIGNED_ERROR.getCode(),
                    BadReqExceptionMsg.PRE_SIGNED_ERROR.getMessage());
        }
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
                predicates.add(cb.equal(root.get(PredicateFieldName.MEDIA_TYPE.getName()), MediaType.valueOf(req.getMediaType())));
            }
            if (SecurityUtils.getCurrentUserId() != null){
                predicates.add(cb.equal(root.get(PredicateFieldName.USER_ID.getName()), SecurityUtils.getCurrentUserId()));
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
            throw new BusinessException(BadReqExceptionMsg.MEDIA_IS_NOT_EXIST.getCode(),
                    BadReqExceptionMsg.MEDIA_IS_NOT_EXIST.getMessage());
        }
        return mediaOptional.get();
    }
}
