package com.self_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDTO {
    private int chunkNumber;
    private int chunkSize;
    private int currentChunkSize;
    private long totalSize;
    private String identifier;
    private String filename;
    private String relativePath;
    private int totalChunks;
    private int pid;
    private MultipartFile file;
    private int userId;
}
