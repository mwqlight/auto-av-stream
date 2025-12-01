package com.avstream.media.service.impl;

import com.avstream.media.dto.request.UploadRequest;
import com.avstream.media.dto.response.MediaInfoResponse;
import com.avstream.media.dto.response.UploadResponse;
import com.avstream.media.entity.MediaFile;
import com.avstream.media.entity.MediaMetadata;
import com.avstream.media.entity.MediaThumbnail;
import com.avstream.media.entity.MediaTranscode;
import com.avstream.media.exception.BusinessException;
import com.avstream.media.repository.*;
import com.avstream.media.service.MediaHealthInfo;
import com.avstream.media.service.MediaService;
import com.avstream.media.service.StorageService;
import com.avstream.media.service.TranscodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 媒体文件服务实现类
 * 
 * @author AV Stream Team
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaServiceImpl.class);

    private final MediaFileRepository mediaFileRepository;
    private final MediaMetadataRepository mediaMetadataRepository;
    private final MediaThumbnailRepository mediaThumbnailRepository;
    private final MediaTranscodeRepository mediaTranscodeRepository;
    private final StorageService storageService;
    private final TranscodeService transcodeService;

    @Override
    @Transactional
    public UploadResponse uploadFile(MultipartFile file, UploadRequest request, Long userId) {
        try {
            // 检查上传权限
            if (!checkUploadPermission(userId)) {
                throw new BusinessException("上传权限不足");
            }

            // 检查文件大小限制
            if (!checkFileSizeLimit(file.getSize(), userId)) {
                throw new BusinessException("文件大小超出限制");
            }

            // 检查文件类型支持
            if (!checkFileTypeSupport(file.getOriginalFilename())) {
                throw new BusinessException("不支持的文件类型");
            }

            // 生成文件UUID
            String fileUuid = UUID.randomUUID().toString();

            // 创建媒体文件记录
            MediaFile mediaFile = createMediaFile(file, request, userId, fileUuid);

            // 上传文件到存储
            String filePath = storageService.uploadFile(file, fileUuid);
            mediaFile.setFilePath(filePath);
            mediaFile.setUploadProgress(100);
            mediaFile.setUploadCompletedAt(LocalDateTime.now());
            mediaFile.setStatus(MediaFile.MediaStatus.UPLOADED);

            MediaFile savedFile = mediaFileRepository.save(mediaFile);

            // 异步处理上传完成的文件
            processUploadedFile(fileUuid);

            return UploadResponse.builder()
                    .fileUuid(fileUuid)
                    .status(UploadResponse.UploadStatus.COMPLETED)
                    .progress(100)
                    .message("文件上传成功")
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UploadResponse uploadChunk(MultipartFile chunk, String fileUuid, Integer chunkIndex, Integer totalChunks, Long userId) {
        try {
            // 检查文件UUID是否存在
            Optional<MediaFile> existingFile = mediaFileRepository.findByFileUuid(fileUuid);
            MediaFile mediaFile;

            if (existingFile.isPresent()) {
                mediaFile = existingFile.get();
                // 验证用户权限
                if (!mediaFile.getUserId().equals(userId)) {
                    throw new BusinessException("无权操作此文件");
                }
            } else {
                // 创建新的媒体文件记录
                mediaFile = createChunkMediaFile(chunk, userId, fileUuid, totalChunks);
            }

            // 上传分片
            storageService.uploadChunk(chunk, fileUuid, chunkIndex);

            // 更新上传进度
            int progress = (int) ((chunkIndex + 1) * 100.0 / totalChunks);
            mediaFile.setUploadProgress(progress);
            mediaFileRepository.save(mediaFile);

            return UploadResponse.builder()
                    .fileUuid(fileUuid)
                    .status(UploadResponse.UploadStatus.UPLOADING)
                    .progress(progress)
                    .message(String.format("分片 %d/%d 上传成功", chunkIndex + 1, totalChunks))
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("分片上传失败: {}", e.getMessage(), e);
            throw new BusinessException("分片上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UploadResponse mergeChunks(String fileUuid, Long userId) {
        try {
            MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                    .orElseThrow(() -> new BusinessException("文件不存在"));

            // 验证用户权限
            if (!mediaFile.getUserId().equals(userId)) {
                throw new BusinessException("无权操作此文件");
            }

            // 合并分片
            String filePath = storageService.mergeChunks(fileUuid, mediaFile.getTotalChunks());
            mediaFile.setFilePath(filePath);
            mediaFile.setUploadProgress(100);
            mediaFile.setUploadCompletedAt(LocalDateTime.now());
            mediaFile.setStatus(MediaFile.MediaStatus.UPLOADED);
            mediaFileRepository.save(mediaFile);

            // 异步处理上传完成的文件
            processUploadedFile(fileUuid);

            return UploadResponse.builder()
                    .fileUuid(fileUuid)
                    .status(UploadResponse.UploadStatus.COMPLETED)
                    .progress(100)
                    .message("文件合并成功")
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("文件合并失败: {}", e.getMessage(), e);
            throw new BusinessException("文件合并失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MediaInfoResponse getMediaInfo(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId) && !mediaFile.getIsPublic()) {
            throw new BusinessException("无权访问此文件");
        }

        return buildMediaInfoResponse(mediaFile);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaInfoResponse getPublicMediaInfo(String fileUuid) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证是否为公开文件
        if (!mediaFile.getIsPublic()) {
            throw new BusinessException("文件未公开");
        }

        return buildMediaInfoResponse(mediaFile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MediaInfoResponse> getUserMediaFiles(Long userId, Pageable pageable) {
        Page<MediaFile> mediaFiles = mediaFileRepository.findByUserId(userId, pageable);
        return mediaFiles.map(this::buildMediaInfoResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MediaInfoResponse> searchMediaFiles(String keyword, Long userId, Pageable pageable) {
        // 实现搜索逻辑
        List<MediaFile> mediaFiles = mediaFileRepository.findByFilenameContainingIgnoreCase(keyword);
        List<MediaFile> userFiles = mediaFiles.stream()
                .filter(file -> file.getUserId().equals(userId))
                .collect(Collectors.toList());

        // 转换为分页结果
        return new org.springframework.data.domain.PageImpl<>(
                userFiles.stream().map(this::buildMediaInfoResponse).collect(Collectors.toList()),
                pageable,
                userFiles.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MediaInfoResponse> filterMediaFilesByType(MediaFile.FileType fileType, Long userId, Pageable pageable) {
        List<MediaFile> mediaFiles = mediaFileRepository.findByUserIdAndFileType(userId, fileType);
        return new org.springframework.data.domain.PageImpl<>(
                mediaFiles.stream().map(this::buildMediaInfoResponse).collect(Collectors.toList()),
                pageable,
                mediaFiles.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MediaInfoResponse> filterMediaFilesByStatus(MediaFile.MediaStatus status, Long userId, Pageable pageable) {
        List<MediaFile> mediaFiles = mediaFileRepository.findByUserIdAndStatus(userId, status);
        return new org.springframework.data.domain.PageImpl<>(
                mediaFiles.stream().map(this::buildMediaInfoResponse).collect(Collectors.toList()),
                pageable,
                mediaFiles.size()
        );
    }

    @Override
    @Transactional
    public MediaInfoResponse updateMediaInfo(String fileUuid, String filename, String description, Boolean isPublic, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此文件");
        }

        if (filename != null) {
            mediaFile.setFilename(filename);
        }
        if (description != null) {
            mediaFile.setDescription(description);
        }
        if (isPublic != null) {
            mediaFile.setIsPublic(isPublic);
        }

        mediaFile.setUpdatedAt(LocalDateTime.now());
        MediaFile updatedFile = mediaFileRepository.save(mediaFile);

        return buildMediaInfoResponse(updatedFile);
    }

    @Override
    @Transactional
    public void deleteMediaFile(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此文件");
        }

        mediaFileRepository.softDelete(mediaFile.getId());
        log.info("用户 {} 删除了文件 {}", userId, fileUuid);
    }

    @Override
    @Transactional
    public void batchDeleteMediaFiles(List<String> fileUuids, Long userId) {
        for (String fileUuid : fileUuids) {
            deleteMediaFile(fileUuid, userId);
        }
    }

    @Override
    @Transactional
    public void restoreMediaFile(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此文件");
        }

        mediaFileRepository.restore(mediaFile.getId());
        log.info("用户 {} 恢复了文件 {}", userId, fileUuid);
    }

    @Override
    @Transactional
    public void permanentDeleteMediaFile(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此文件");
        }

        try {
            // 删除存储中的文件
            storageService.deleteFile(mediaFile.getFilePath());
            
            // 删除数据库记录
            mediaFileRepository.delete(mediaFile);
            
            log.info("用户 {} 永久删除了文件 {}", userId, fileUuid);
        } catch (IOException e) {
            log.error("永久删除文件失败: {}", e.getMessage(), e);
            throw new BusinessException("永久删除文件失败");
        }
    }

    // 其他方法实现...
    @Override
    public MediaFile downloadMediaFile(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId) && !mediaFile.getIsPublic()) {
            throw new BusinessException("无权下载此文件");
        }

        return mediaFile;
    }

    @Override
    public MediaFile downloadPublicMediaFile(String fileUuid) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证是否为公开文件
        if (!mediaFile.getIsPublic()) {
            throw new BusinessException("文件未公开");
        }

        return mediaFile;
    }

    @Override
    public String getPreviewUrl(String fileUuid, Long userId) {
        // 实现预览URL生成逻辑
        return storageService.generatePreviewUrl(fileUuid);
    }

    @Override
    public String getPlayUrl(String fileUuid, Long userId) {
        // 实现播放URL生成逻辑
        return storageService.generatePlayUrl(fileUuid);
    }

    @Override
    public String getDownloadUrl(String fileUuid, Long userId) {
        // 实现下载URL生成逻辑
        return storageService.generateDownloadUrl(fileUuid);
    }

    @Override
    public boolean checkUploadPermission(Long userId) {
        // 实现上传权限检查逻辑
        return true; // 默认允许上传
    }

    @Override
    public boolean checkFileSizeLimit(Long fileSize, Long userId) {
        // 实现文件大小限制检查逻辑
        StorageUsage usage = getUserStorageUsage(userId);
        Long remaining = usage.getTotalStorage() - usage.getUsedStorage();
        return fileSize <= remaining;
    }

    @Override
    public boolean checkFileTypeSupport(String filename) {
        // 实现文件类型支持检查逻辑
        String extension = getFileExtension(filename).toLowerCase();
        Set<String> supportedExtensions = Set.of("mp4", "avi", "mov", "mkv", "mp3", "wav", "flac", "jpg", "png", "gif");
        return supportedExtensions.contains(extension);
    }

    @Override
    public StorageUsage getUserStorageUsage(Long userId) {
        Long usedStorage = mediaFileRepository.sumFileSizeByUserId(userId);
        Long totalStorage = 10L * 1024 * 1024 * 1024; // 10GB默认存储空间
        return new StorageUsage(usedStorage, totalStorage);
    }

    @Override
    public void cleanupExpiredTemporaryFiles() {
        // 实现清理过期临时文件逻辑
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<MediaFile> expiredFiles = mediaFileRepository.findStuckUploads(threshold);
        
        for (MediaFile file : expiredFiles) {
            try {
                storageService.deleteFile(file.getFilePath());
                mediaFileRepository.delete(file);
                log.info("清理过期临时文件: {}", file.getFileUuid());
            } catch (IOException e) {
                log.error("清理文件失败: {}", file.getFileUuid(), e);
            }
        }
    }

    @Override
    public void processUploadedFile(String fileUuid) {
        // 异步处理上传完成的文件（元数据提取、缩略图生成等）
        log.info("开始处理上传完成的文件: {}", fileUuid);
        // 这里可以调用其他服务进行处理
    }

    @Override
    public void handleUploadFailed(String fileUuid, String errorMessage) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        mediaFile.setStatus(MediaFile.MediaStatus.FAILED);
        mediaFile.setErrorMessage(errorMessage);
        mediaFileRepository.save(mediaFile);
        
        log.error("文件上传失败: {}, 错误: {}", fileUuid, errorMessage);
    }

    @Override
    public Integer getUploadProgress(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此文件");
        }

        return mediaFile.getUploadProgress();
    }

    @Override
    public void cancelUpload(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此文件");
        }

        try {
            // 删除已上传的分片
            storageService.cancelUpload(fileUuid);
            
            // 删除数据库记录
            mediaFileRepository.delete(mediaFile);
            
            log.info("用户 {} 取消了文件上传: {}", userId, fileUuid);
        } catch (IOException e) {
            log.error("取消上传失败: {}", e.getMessage(), e);
            throw new BusinessException("取消上传失败");
        }
    }

    @Override
    public void retryFailedUpload(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此文件");
        }

        if (mediaFile.getStatus() != MediaFile.MediaStatus.FAILED) {
            throw new BusinessException("只有失败的文件才能重试");
        }

        mediaFile.setStatus(MediaFile.MediaStatus.UPLOADING);
        mediaFile.setUploadProgress(0);
        mediaFile.setErrorMessage(null);
        mediaFileRepository.save(mediaFile);
        
        log.info("用户 {} 重试上传文件: {}", userId, fileUuid);
    }

    @Override
    public Long countUserFiles(Long userId) {
        return mediaFileRepository.countByUserId(userId);
    }

    @Override
    public Long sumUserFileSize(Long userId) {
        return mediaFileRepository.sumFileSizeByUserId(userId);
    }

    @Override
    public List<MediaInfoResponse> getPopularMediaFiles(int limit) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<MediaFile> popularFiles = mediaFileRepository.findPopularFiles(pageable);
        return popularFiles.stream().map(this::buildMediaInfoResponse).collect(Collectors.toList());
    }

    @Override
    public List<MediaInfoResponse> getRecentMediaFiles(int limit) {
        // 实现获取最近上传文件的逻辑
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<MediaFile> recentFiles = mediaFileRepository.findRecentFiles(null, pageable);
        return recentFiles.stream().map(this::buildMediaInfoResponse).collect(Collectors.toList());
    }

    @Override
    public String getFilePath(String fileUuid, Long userId) {
        MediaFile mediaFile = mediaFileRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException("文件不存在"));

        // 验证用户权限
        if (!mediaFile.getUserId().equals(userId)) {
            throw new BusinessException("无权访问此文件");
        }

        return mediaFile.getFilePath();
    }

    // 辅助方法
    private MediaFile createMediaFile(MultipartFile file, UploadRequest request, Long userId, String fileUuid) {
        return MediaFile.builder()
                .fileUuid(fileUuid)
                .filename(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(determineFileType(file.getOriginalFilename()))
                .userId(userId)
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .status(MediaFile.MediaStatus.UPLOADING)
                .uploadProgress(0)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private MediaFile createChunkMediaFile(MultipartFile chunk, Long userId, String fileUuid, Integer totalChunks) {
        return MediaFile.builder()
                .fileUuid(fileUuid)
                .filename(chunk.getOriginalFilename())
                .fileSize(0L) // 初始大小为0，合并后更新
                .fileType(determineFileType(chunk.getOriginalFilename()))
                .userId(userId)
                .totalChunks(totalChunks)
                .status(MediaFile.MediaStatus.UPLOADING)
                .uploadProgress(0)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private MediaFile.FileType determineFileType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        if (Set.of("mp4", "avi", "mov", "mkv", "flv", "wmv").contains(extension)) {
            return MediaFile.FileType.VIDEO;
        } else if (Set.of("mp3", "wav", "flac", "aac", "ogg").contains(extension)) {
            return MediaFile.FileType.AUDIO;
        } else if (Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension)) {
            return MediaFile.FileType.IMAGE;
        } else {
            return MediaFile.FileType.OTHER;
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }

    private MediaInfoResponse buildMediaInfoResponse(MediaFile mediaFile) {
        // 构建媒体信息响应对象
        return MediaInfoResponse.builder()
                .fileUuid(mediaFile.getFileUuid())
                .filename(mediaFile.getFilename())
                .fileSize(mediaFile.getFileSize())
                .fileType(mediaFile.getFileType().toString())
                .status(mediaFile.getStatus().toString())
                .isPublic(mediaFile.getIsPublic())
                .description(mediaFile.getDescription())
                .uploadProgress(mediaFile.getUploadProgress())
                .createdAt(mediaFile.getCreatedAt())
                .updatedAt(mediaFile.getUpdatedAt())
                .build();
    }

    @Override
    public boolean healthCheck() {
        try {
            // 检查数据库连接
            boolean dbConnected = mediaFileRepository.count() >= 0;
            
            // 检查存储服务
            boolean storageAvailable = storageService.healthCheck();
            
            // 检查转码服务
            boolean transcodeAvailable = transcodeService.healthCheck();
            
            // 所有服务都可用才认为服务健康
            return dbConnected && storageAvailable && transcodeAvailable;
        } catch (Exception e) {
            log.error("媒体服务健康检查失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public MediaHealthInfo getMediaHealthInfo() {
        try {
            // 检查数据库连接
            boolean dbConnected = true;
            try {
                mediaFileRepository.count();
            } catch (Exception e) {
                dbConnected = false;
                log.warn("数据库连接检查失败: {}", e.getMessage());
            }
            
            // 检查存储服务
            boolean storageAvailable = storageService.healthCheck();
            
            // 检查转码服务
            boolean transcodeAvailable = transcodeService.healthCheck();
            
            // 统计媒体文件信息
            long totalMediaFiles = mediaFileRepository.count();
            
            // 使用findByStatus方法获取不同状态的媒体文件数量
            List<MediaFile> processedFiles = mediaFileRepository.findByStatus(MediaFile.MediaStatus.PROCESSED);
            long activeMediaFiles = processedFiles.size();
            
            // 统计转码任务信息（这里需要根据实际实现调整）
            long processingTranscodeTasks = 0;
            long failedTranscodeTasks = 0;
            
            // 如果所有核心服务都可用，则服务健康
            boolean serviceAvailable = dbConnected && storageAvailable && transcodeAvailable;
            
            if (serviceAvailable) {
                return MediaHealthInfo.healthy(
                    dbConnected, storageAvailable, transcodeAvailable,
                    totalMediaFiles, activeMediaFiles, processingTranscodeTasks, failedTranscodeTasks
                );
            } else {
                String errorMessage = "服务不可用: ";
                if (!dbConnected) errorMessage += "数据库连接失败; ";
                if (!storageAvailable) errorMessage += "存储服务不可用; ";
                if (!transcodeAvailable) errorMessage += "转码服务不可用; ";
                return MediaHealthInfo.unhealthy(errorMessage.trim());
            }
        } catch (Exception e) {
            log.error("获取媒体服务健康信息失败: {}", e.getMessage(), e);
            return MediaHealthInfo.unhealthy("获取健康信息时发生异常: " + e.getMessage());
        }
    }

    @Override
    public void cleanupExpiredMediaFiles() {
        // 清理过期的媒体文件（例如30天前的临时文件或已删除文件）
        // 实际项目中需要根据业务需求实现
        log.info("执行媒体文件清理");
    }
}