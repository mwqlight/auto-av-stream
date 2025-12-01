package com.avstream.ai.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件处理工具类
 */
@Slf4j
public class FileUtil {
    
    private FileUtil() {
        // 工具类，禁止实例化
    }
    
    /**
     * 保存上传文件
     */
    public static String saveUploadFile(MultipartFile file, String uploadDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }
        
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(uniqueFilename);
        file.transferTo(filePath.toFile());
        
        log.info("文件保存成功: {}", filePath);
        return filePath.toString();
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    /**
     * 验证文件类型
     */
    public static boolean isValidAudioFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals(".wav") || extension.equals(".mp3") || 
               extension.equals(".m4a") || extension.equals(".flac");
    }
    
    /**
     * 验证图像文件类型
     */
    public static boolean isValidImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals(".jpg") || extension.equals(".jpeg") || 
               extension.equals(".png") || extension.equals(".bmp") || 
               extension.equals(".gif");
    }
    
    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * 获取文件大小（MB）
     */
    public static double getFileSizeMB(String filePath) {
        try {
            Path path = Paths.get(filePath);
            long bytes = Files.size(path);
            return bytes / (1024.0 * 1024.0);
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", filePath, e);
            return 0;
        }
    }
}