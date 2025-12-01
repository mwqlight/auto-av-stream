package com.avstream.media.service;

import com.avstream.media.entity.MediaTranscode;

/**
 * 转码服务接口
 * 
 * @author AV Stream Team
 */
public interface TranscodeService {

    /**
     * 创建转码任务
     */
    MediaTranscode createTranscodeTask(String fileUuid, String templateName, Long userId);

    /**
     * 开始转码任务
     */
    void startTranscodeTask(String transcodeUuid);

    /**
     * 取消转码任务
     */
    void cancelTranscodeTask(String transcodeUuid);

    /**
     * 获取转码进度
     */
    Integer getTranscodeProgress(String transcodeUuid);

    /**
     * 获取转码任务状态
     */
    MediaTranscode.TranscodeStatus getTranscodeStatus(String transcodeUuid);

    /**
     * 重试失败的转码任务
     */
    void retryTranscodeTask(String transcodeUuid);

    /**
     * 删除转码任务
     */
    void deleteTranscodeTask(String transcodeUuid);

    /**
     * 获取转码模板列表
     */
    java.util.List<String> getTranscodeTemplates();

    /**
     * 获取支持的输出格式
     */
    java.util.List<String> getSupportedOutputFormats();

    /**
     * 检查转码任务是否正在处理
     */
    boolean isTranscodeProcessing(String fileUuid);

    /**
     * 获取文件的转码历史
     */
    java.util.List<MediaTranscode> getTranscodeHistory(String fileUuid);

    /**
     * 批量创建转码任务
     */
    java.util.List<MediaTranscode> batchCreateTranscodeTasks(java.util.List<String> fileUuids, String templateName, Long userId);

    /**
     * 批量取消转码任务
     */
    void batchCancelTranscodeTasks(java.util.List<String> transcodeUuids);

    /**
     * 清理过期转码任务
     */
    void cleanupExpiredTranscodeTasks();

    /**
     * 健康检查 - 检查转码服务是否可用
     */
    boolean healthCheck();

    /**
     * 获取转码服务健康信息
     */
    TranscodeHealthInfo getTranscodeHealthInfo();
}