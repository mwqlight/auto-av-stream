package com.avstream.media.controller;

import com.avstream.media.dto.request.UploadRequest;
import com.avstream.media.dto.response.MediaInfoResponse;
import com.avstream.media.dto.response.UploadResponse;
import com.avstream.media.entity.MediaFile;
import com.avstream.media.service.MediaService;
import com.avstream.media.service.FFmpegService;
import com.avstream.media.service.MediaMTXService;
import com.avstream.media.service.WebRTCService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 媒体文件控制器
 * 
 * @author AV Stream Team
 */
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "媒体管理", description = "媒体文件上传、下载、管理接口")
public class MediaController {
    
    private static final Logger log = LoggerFactory.getLogger(MediaController.class);

    private final MediaService mediaService;
    private final FFmpegService ffmpegService;
    private final MediaMTXService mediaMTXService;
    private final WebRTCService webRTCService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传媒体文件", description = "上传单个媒体文件")
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute UploadRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 开始上传文件: {}", userId, file.getOriginalFilename());
        
        UploadResponse response = mediaService.uploadFile(file, request, userId);
        
        log.info("用户 {} 文件上传完成: {}", userId, response.getFileUuid());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "分片上传媒体文件", description = "分片上传大文件")
    public ResponseEntity<UploadResponse> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("fileUuid") String fileUuid,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("totalChunks") Integer totalChunks,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.debug("用户 {} 上传分片 {} of {} for file {}", userId, chunkIndex + 1, totalChunks, fileUuid);
        
        UploadResponse response = mediaService.uploadChunk(chunk, fileUuid, chunkIndex, totalChunks, userId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/merge")
    @Operation(summary = "合并分片文件", description = "合并所有分片为完整文件")
    public ResponseEntity<UploadResponse> mergeChunks(
            @RequestParam("fileUuid") String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 开始合并分片文件: {}", userId, fileUuid);
        
        UploadResponse response = mediaService.mergeChunks(fileUuid, userId);
        
        log.info("用户 {} 分片文件合并完成: {}", userId, fileUuid);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{fileUuid}")
    @Operation(summary = "获取媒体文件信息", description = "获取指定媒体文件的详细信息")
    public ResponseEntity<MediaInfoResponse> getMediaInfo(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        MediaInfoResponse response = mediaService.getMediaInfo(fileUuid, userId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/info/{fileUuid}")
    @Operation(summary = "获取公开媒体文件信息", description = "获取公开媒体文件的详细信息（无需认证）")
    public ResponseEntity<MediaInfoResponse> getPublicMediaInfo(
            @PathVariable String fileUuid) {
        
        MediaInfoResponse response = mediaService.getPublicMediaInfo(fileUuid);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户媒体文件列表", description = "分页获取用户的媒体文件列表")
    public ResponseEntity<Page<MediaInfoResponse>> getUserMediaFiles(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<MediaInfoResponse> response = mediaService.getUserMediaFiles(userId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索媒体文件", description = "根据关键词搜索用户的媒体文件")
    public ResponseEntity<Page<MediaInfoResponse>> searchMediaFiles(
            @RequestParam String keyword,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaInfoResponse> response = mediaService.searchMediaFiles(keyword, userId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/type")
    @Operation(summary = "按类型筛选媒体文件", description = "根据文件类型筛选用户的媒体文件")
    public ResponseEntity<Page<MediaInfoResponse>> filterMediaFilesByType(
            @RequestParam MediaFile.FileType fileType,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaInfoResponse> response = mediaService.filterMediaFilesByType(fileType, userId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/status")
    @Operation(summary = "按状态筛选媒体文件", description = "根据处理状态筛选用户的媒体文件")
    public ResponseEntity<Page<MediaInfoResponse>> filterMediaFilesByStatus(
            @RequestParam MediaFile.MediaStatus status,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaInfoResponse> response = mediaService.filterMediaFilesByStatus(status, userId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/info/{fileUuid}")
    @Operation(summary = "更新媒体文件信息", description = "更新媒体文件的文件名、描述、公开状态等信息")
    public ResponseEntity<MediaInfoResponse> updateMediaInfo(
            @PathVariable String fileUuid,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean isPublic,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 更新文件信息: {}", userId, fileUuid);
        
        MediaInfoResponse response = mediaService.updateMediaInfo(fileUuid, filename, description, isPublic, userId);
        
        log.info("用户 {} 文件信息更新完成: {}", userId, fileUuid);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileUuid}")
    @Operation(summary = "删除媒体文件", description = "软删除媒体文件（可恢复）")
    public ResponseEntity<Void> deleteMediaFile(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 删除文件: {}", userId, fileUuid);
        
        mediaService.deleteMediaFile(fileUuid, userId);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除媒体文件", description = "批量软删除多个媒体文件")
    public ResponseEntity<Void> batchDeleteMediaFiles(
            @RequestBody List<String> fileUuids,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 批量删除文件: {}", userId, fileUuids.size());
        
        mediaService.batchDeleteMediaFiles(fileUuids, userId);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/restore/{fileUuid}")
    @Operation(summary = "恢复媒体文件", description = "恢复已删除的媒体文件")
    public ResponseEntity<Void> restoreMediaFile(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 恢复文件: {}", userId, fileUuid);
        
        mediaService.restoreMediaFile(fileUuid, userId);
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/permanent/{fileUuid}")
    @Operation(summary = "永久删除媒体文件", description = "永久删除媒体文件（不可恢复）")
    public ResponseEntity<Void> permanentDeleteMediaFile(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.warn("用户 {} 永久删除文件: {}", userId, fileUuid);
        
        mediaService.permanentDeleteMediaFile(fileUuid, userId);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{fileUuid}")
    @Operation(summary = "下载媒体文件", description = "下载媒体文件内容")
    public void downloadMediaFile(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId,
            HttpServletResponse response) throws IOException {
        
        log.info("用户 {} 下载文件: {}", userId, fileUuid);
        
        MediaFile mediaFile = mediaService.downloadMediaFile(fileUuid, userId);
        
        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
            "attachment; filename=\"" + mediaFile.getFilename() + "\"");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mediaFile.getFileSize()));
        
        // 这里应该实现文件流传输逻辑
        // 由于存储服务未实现，暂时返回空响应
        response.getOutputStream().flush();
    }

    @GetMapping("/public/download/{fileUuid}")
    @Operation(summary = "下载公开媒体文件", description = "下载公开媒体文件内容（无需认证）")
    public void downloadPublicMediaFile(
            @PathVariable String fileUuid,
            HttpServletResponse response) throws IOException {
        
        log.info("下载公开文件: {}", fileUuid);
        
        MediaFile mediaFile = mediaService.downloadPublicMediaFile(fileUuid);
        
        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
            "attachment; filename=\"" + mediaFile.getFilename() + "\"");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mediaFile.getFileSize()));
        
        // 这里应该实现文件流传输逻辑
        response.getOutputStream().flush();
    }

    @GetMapping("/preview/{fileUuid}")
    @Operation(summary = "获取预览URL", description = "获取媒体文件的预览URL")
    public ResponseEntity<String> getPreviewUrl(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        String previewUrl = mediaService.getPreviewUrl(fileUuid, userId);
        
        return ResponseEntity.ok(previewUrl);
    }

    @GetMapping("/play/{fileUuid}")
    @Operation(summary = "获取播放URL", description = "获取媒体文件的播放URL")
    public ResponseEntity<String> getPlayUrl(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        String playUrl = mediaService.getPlayUrl(fileUuid, userId);
        
        return ResponseEntity.ok(playUrl);
    }

    @GetMapping("/download-url/{fileUuid}")
    @Operation(summary = "获取下载URL", description = "获取媒体文件的下载URL")
    public ResponseEntity<String> getDownloadUrl(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        String downloadUrl = mediaService.getDownloadUrl(fileUuid, userId);
        
        return ResponseEntity.ok(downloadUrl);
    }

    @GetMapping("/upload/progress/{fileUuid}")
    @Operation(summary = "获取上传进度", description = "获取文件上传的进度百分比")
    public ResponseEntity<Integer> getUploadProgress(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        Integer progress = mediaService.getUploadProgress(fileUuid, userId);
        
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/upload/cancel/{fileUuid}")
    @Operation(summary = "取消上传", description = "取消正在进行的文件上传")
    public ResponseEntity<Void> cancelUpload(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 取消上传文件: {}", userId, fileUuid);
        
        mediaService.cancelUpload(fileUuid, userId);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload/retry/{fileUuid}")
    @Operation(summary = "重试上传", description = "重试失败的文件上传")
    public ResponseEntity<Void> retryFailedUpload(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("用户 {} 重试上传文件: {}", userId, fileUuid);
        
        mediaService.retryFailedUpload(fileUuid, userId);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storage/usage")
    @Operation(summary = "获取存储使用情况", description = "获取用户的存储空间使用情况")
    public ResponseEntity<MediaService.StorageUsage> getStorageUsage(
            @RequestHeader("X-User-Id") Long userId) {
        
        MediaService.StorageUsage usage = mediaService.getUserStorageUsage(userId);
        
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门媒体文件", description = "获取热门媒体文件列表")
    public ResponseEntity<List<MediaInfoResponse>> getPopularMediaFiles(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<MediaInfoResponse> response = mediaService.getPopularMediaFiles(limit);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最近上传的媒体文件", description = "获取最近上传的媒体文件列表")
    public ResponseEntity<List<MediaInfoResponse>> getRecentMediaFiles(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<MediaInfoResponse> response = mediaService.getRecentMediaFiles(limit);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cleanup/temporary")
    @Operation(summary = "清理临时文件", description = "清理过期的临时文件（管理员功能）")
    public ResponseEntity<Void> cleanupTemporaryFiles() {
        
        log.info("开始清理过期临时文件");
        
        mediaService.cleanupExpiredTemporaryFiles();
        
        log.info("过期临时文件清理完成");
        
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("媒体管理服务异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("服务器内部错误: " + e.getMessage());
    }

    // ========== 音视频处理相关API ==========

    @PostMapping("/transcode")
    @Operation(summary = "视频转码", description = "对视频文件进行转码处理")
    public ResponseEntity<Map<String, Object>> transcodeVideo(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            String fileUuid = (String) request.get("fileUuid");
            String format = (String) request.get("format");
            
            if (fileUuid == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "文件UUID不能为空",
                    "timestamp", System.currentTimeMillis()
                ));
            }

            // 获取文件路径
            String filePath = mediaService.getFilePath(fileUuid, userId);
            String outputPath = filePath + ".transcoded." + format;

            CompletableFuture<String> transcodeFuture = ffmpegService.transcodeVideo(
                filePath, outputPath, "libx264", "medium", "23");

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "转码任务已提交",
                "data", Map.of(
                    "fileUuid", fileUuid,
                    "format", format,
                    "status", "processing"
                ),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("视频转码失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "视频转码失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/thumbnail")
    @Operation(summary = "提取缩略图", description = "从视频文件中提取缩略图")
    public ResponseEntity<Map<String, Object>> extractThumbnail(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            String fileUuid = (String) request.get("fileUuid");
            String time = (String) request.get("time");
            
            if (fileUuid == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "文件UUID不能为空",
                    "timestamp", System.currentTimeMillis()
                ));
            }

            String filePath = mediaService.getFilePath(fileUuid, userId);
            String outputPath = filePath + ".thumbnail.jpg";

            CompletableFuture<String> thumbnailFuture = ffmpegService.extractThumbnail(
                filePath, outputPath, 320, 240, time != null ? time : "00:00:01");

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "缩略图提取任务已提交",
                "data", Map.of(
                    "fileUuid", fileUuid,
                    "time", time != null ? time : "00:00:01",
                    "status", "processing"
                ),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("缩略图提取失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "缩略图提取失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/stream/create")
    @Operation(summary = "创建直播流", description = "创建RTMP/HLS/WebRTC直播流")
    public ResponseEntity<Map<String, Object>> createStream(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            String streamId = (String) request.get("streamId");
            String sourceUrl = (String) request.get("sourceUrl");
            
            if (streamId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "流ID不能为空",
                    "timestamp", System.currentTimeMillis()
                ));
            }

            // 检查MediaMTX服务状态
            if (!mediaMTXService.checkServiceStatus()) {
                return ResponseEntity.internalServerError().body(Map.of(
                    "code", 503,
                    "message", "流媒体服务器不可用",
                    "timestamp", System.currentTimeMillis()
                ));
            }

            Map<String, String> streamUrls = mediaMTXService.createCompleteStream(streamId, sourceUrl);

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "直播流创建成功",
                "data", Map.of(
                    "streamId", streamId,
                    "urls", streamUrls,
                    "status", "created"
                ),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("创建直播流失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "创建直播流失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @DeleteMapping("/stream/{streamId}")
    @Operation(summary = "删除直播流", description = "删除指定的直播流")
    public ResponseEntity<Map<String, Object>> deleteStream(
            @PathVariable String streamId) {
        
        try {
            boolean deleted = mediaMTXService.deleteStream(streamId);

            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "直播流删除成功",
                    "data", Map.of(
                        "streamId", streamId,
                        "status", "deleted"
                    ),
                    "timestamp", System.currentTimeMillis()
                ));
            } else {
                return ResponseEntity.internalServerError().body(Map.of(
                    "code", 500,
                    "message", "直播流删除失败",
                    "timestamp", System.currentTimeMillis()
                ));
            }

        } catch (Exception e) {
            log.error("删除直播流失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "删除直播流失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/stream/{streamId}/status")
    @Operation(summary = "获取流状态", description = "获取直播流的当前状态")
    public ResponseEntity<Map<String, Object>> getStreamStatus(
            @PathVariable String streamId) {
        
        try {
            Map<String, Object> status = mediaMTXService.getStreamStatus(streamId);

            if (status != null) {
                return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "获取流状态成功",
                    "data", status,
                    "timestamp", System.currentTimeMillis()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("获取流状态失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "获取流状态失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/streams")
    @Operation(summary = "获取所有流", description = "获取所有活动的直播流")
    public ResponseEntity<Map<String, Object>> getAllStreams() {
        
        try {
            Map<String, Object> streams = mediaMTXService.getAllStreams();

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取所有流成功",
                "data", streams != null ? streams : Map.of(),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("获取所有流失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "获取所有流失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/extract-audio")
    @Operation(summary = "提取音频", description = "从视频文件中提取音频")
    public ResponseEntity<Map<String, Object>> extractAudio(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            String fileUuid = (String) request.get("fileUuid");
            String format = (String) request.get("format");
            
            if (fileUuid == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "文件UUID不能为空",
                    "timestamp", System.currentTimeMillis()
                ));
            }

            String filePath = mediaService.getFilePath(fileUuid, userId);
            String outputPath = filePath + ".audio." + (format != null ? format : "mp3");

            CompletableFuture<String> audioFuture = ffmpegService.extractAudio(
                filePath, outputPath, format != null ? format : "mp3", "128k");

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "音频提取任务已提交",
                "data", Map.of(
                    "fileUuid", fileUuid,
                    "format", format != null ? format : "mp3",
                    "status", "processing"
                ),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("音频提取失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "音频提取失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/clip")
    @Operation(summary = "视频剪辑", description = "对视频文件进行剪辑处理")
    public ResponseEntity<Map<String, Object>> clipVideo(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            String fileUuid = (String) request.get("fileUuid");
            String startTime = (String) request.get("startTime");
            String duration = (String) request.get("duration");
            
            if (fileUuid == null || startTime == null || duration == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "文件UUID、开始时间和持续时间不能为空",
                    "timestamp", System.currentTimeMillis()
                ));
            }

            String filePath = mediaService.getFilePath(fileUuid, userId);
            String outputPath = filePath + ".clipped.mp4";

            CompletableFuture<String> clipFuture = ffmpegService.clipVideo(
                filePath, outputPath, startTime, duration);

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "视频剪辑任务已提交",
                "data", Map.of(
                    "fileUuid", fileUuid,
                    "startTime", startTime,
                    "duration", duration,
                    "status", "processing"
                ),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("视频剪辑失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "视频剪辑失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/av/status")
    @Operation(summary = "获取音视频处理状态", description = "获取音视频处理服务的状态")
    public ResponseEntity<Map<String, Object>> getAVStatus() {
        
        try {
            boolean mediaMTXStatus = mediaMTXService.checkServiceStatus();
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "音视频处理服务状态获取成功",
                "data", Map.of(
                    "mediaMTX", mediaMTXStatus ? "online" : "offline",
                    "ffmpeg", "available",
                    "webrtc", "available",
                    "timestamp", System.currentTimeMillis()
                ),
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("获取音视频处理状态失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "code", 500,
                "message", "获取音视频处理状态失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}