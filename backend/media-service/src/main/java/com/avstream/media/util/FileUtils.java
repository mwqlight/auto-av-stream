package com.avstream.media.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件工具类
 * 
 * @author AV Stream Team
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
        // 工具类，防止实例化
    }

    /**
     * 生成唯一文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    /**
     * 获取文件名（不含扩展名）
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }

    /**
     * 检查文件类型是否支持
     */
    public static boolean isSupportedFileType(String filename) {
        String extension = getFileExtension(filename);
        return isSupportedImageType(extension) || isSupportedVideoType(extension) || isSupportedAudioType(extension);
    }

    /**
     * 检查是否为支持的图片类型
     */
    public static boolean isSupportedImageType(String extension) {
        String[] supportedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"};
        return containsIgnoreCase(supportedTypes, extension);
    }

    /**
     * 检查是否为支持的视频类型
     */
    public static boolean isSupportedVideoType(String extension) {
        String[] supportedTypes = {"mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp"};
        return containsIgnoreCase(supportedTypes, extension);
    }

    /**
     * 检查是否为支持的音频类型
     */
    public static boolean isSupportedAudioType(String extension) {
        String[] supportedTypes = {"mp3", "wav", "aac", "flac", "ogg", "m4a", "wma"};
        return containsIgnoreCase(supportedTypes, extension);
    }

    /**
     * 检查是否为视频文件
     */
    public static boolean isVideoFile(String filename) {
        return isSupportedVideoType(getFileExtension(filename));
    }

    /**
     * 检查是否为音频文件
     */
    public static boolean isAudioFile(String filename) {
        return isSupportedAudioType(getFileExtension(filename));
    }

    /**
     * 检查是否为图片文件
     */
    public static boolean isImageFile(String filename) {
        return isSupportedImageType(getFileExtension(filename));
    }

    /**
     * 获取文件MIME类型
     */
    public static String getMimeType(String filename) {
        String extension = getFileExtension(filename);
        
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mkv":
                return "video/x-matroska";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "flv":
                return "video/x-flv";
            case "webm":
                return "video/webm";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "aac":
                return "audio/aac";
            case "flac":
                return "audio/flac";
            case "ogg":
                return "audio/ogg";
            case "m4a":
                return "audio/mp4";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 解析文件大小字符串
     */
    public static long parseFileSize(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            return 0;
        }
        
        sizeStr = sizeStr.trim().toUpperCase();
        try {
            if (sizeStr.endsWith("B")) {
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1).trim();
            }
            
            if (sizeStr.endsWith("K") || sizeStr.endsWith("KB")) {
                double value = Double.parseDouble(sizeStr.replaceAll("[K|KB]", "").trim());
                return (long) (value * 1024);
            } else if (sizeStr.endsWith("M") || sizeStr.endsWith("MB")) {
                double value = Double.parseDouble(sizeStr.replaceAll("[M|MB]", "").trim());
                return (long) (value * 1024 * 1024);
            } else if (sizeStr.endsWith("G") || sizeStr.endsWith("GB")) {
                double value = Double.parseDouble(sizeStr.replaceAll("[G|GB]", "").trim());
                return (long) (value * 1024 * 1024 * 1024);
            } else if (sizeStr.endsWith("T") || sizeStr.endsWith("TB")) {
                double value = Double.parseDouble(sizeStr.replaceAll("[T|TB]", "").trim());
                return (long) (value * 1024 * 1024 * 1024 * 1024);
            } else {
                return Long.parseLong(sizeStr);
            }
        } catch (NumberFormatException e) {
            log.warn("无法解析文件大小字符串: {}", sizeStr);
            return 0;
        }
    }

    /**
     * 创建目录（如果不存在）
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建目录: {}", directoryPath);
            }
            return true;
        } catch (IOException e) {
            log.error("创建目录失败: {}", directoryPath, e);
            return false;
        }
    }

    /**
     * 删除文件或目录
     */
    public static boolean deleteFileOrDirectory(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    return deleteDirectory(file);
                } else {
                    return file.delete();
                }
            }
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", path, e);
            return false;
        }
    }

    /**
     * 递归删除目录
     */
    private static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    /**
     * 检查文件是否安全（防止路径遍历攻击）
     */
    public static boolean isSafeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        // 防止路径遍历攻击
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }
        
        // 检查文件名长度
        if (filename.length() > 255) {
            return false;
        }
        
        // 检查文件名是否包含非法字符
        String invalidChars = "[\\/:*?\"<>|]+";
        return !filename.matches(invalidChars);
    }

    /**
     * 验证MultipartFile
     */
    public static boolean validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return false;
        }
        
        return isSafeFilename(originalFilename) && isSupportedFileType(originalFilename);
    }

    /**
     * 获取文件类型分类
     */
    public static String getFileCategory(String filename) {
        if (isImageFile(filename)) {
            return "IMAGE";
        } else if (isVideoFile(filename)) {
            return "VIDEO";
        } else if (isAudioFile(filename)) {
            return "AUDIO";
        } else {
            return "OTHER";
        }
    }

    /**
     * 检查数组是否包含指定元素（忽略大小写）
     */
    private static boolean containsIgnoreCase(String[] array, String target) {
        if (target == null) return false;
        for (String element : array) {
            if (target.equalsIgnoreCase(element)) {
                return true;
            }
        }
        return false;
    }
}