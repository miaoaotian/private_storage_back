package com.self_back.utils;

import java.util.HashMap;
import java.util.Map;

public class FileCategorizer {
    // 定义文件类型的枚举
    public enum FileType {
        FOLDER, VIDEO, AUDIO, CODE, PDF, EXCEL, TXT, DOC, IMAGE, ZIP, OTHER
    }

    // 定义文件类别的枚举
    public enum FileCategory {
        DOCUMENT, VIDEO, AUDIO, IMAGE, OTHER
    }

    // 创建一个映射文件扩展名到FileType的字典
    private static final Map<String, FileType> fileTypeMap = new HashMap<>();
    private static final Map<String, FileCategory> fileCategoryMap = new HashMap<>();

    static {
        // 初始化代码文件类型
        String[] codeExtensions = {"c", "cpp", "java", "py", "go", "js", "html", "css", "vue", "bin"};
        for (String ext : codeExtensions) {
            fileTypeMap.put(ext, FileType.CODE);
            fileCategoryMap.put(ext, FileCategory.DOCUMENT);
        }

        // 初始化压缩包文件类型
        String[] zipExtensions = {"zip", "rar", "7z", "tar", "gz"};
        for (String ext : zipExtensions) {
            fileTypeMap.put(ext, FileType.ZIP);
            fileCategoryMap.put(ext, FileCategory.OTHER);
        }

        // 初始化图片文件类型
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "svg"};
        for (String ext : imageExtensions) {
            fileTypeMap.put(ext, FileType.IMAGE);
            fileCategoryMap.put(ext, FileCategory.IMAGE);
        }

        // 初始化文档文件类型
        String[] documentExtensions = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
        for (String ext : documentExtensions) {
            fileTypeMap.put(ext, FileType.DOC);
            fileCategoryMap.put(ext, FileCategory.DOCUMENT);
        }
        fileTypeMap.put("pdf", FileType.PDF);
        fileTypeMap.put("xls", FileType.EXCEL);
        fileTypeMap.put("xlsx", FileType.EXCEL);
        fileTypeMap.put("txt", FileType.TXT);
        fileTypeMap.put("doc", FileType.DOC);
        fileTypeMap.put("docx", FileType.DOC);
        fileTypeMap.put("ppt", FileType.PDF);
        fileTypeMap.put("pptx", FileType.PDF);

        // 初始化音频文件类型
        String[] audioExtensions = {"mp3", "wav", "aac", "flac", "ogg"};
        for (String ext : audioExtensions) {
            fileTypeMap.put(ext, FileType.AUDIO);
            fileCategoryMap.put(ext, FileCategory.AUDIO);
        }

        // 初始化视频文件类型
        String[] videoExtensions = {"mp4", "avi", "mkv", "mov", "wmv"};
        for (String ext : videoExtensions) {
            fileTypeMap.put(ext, FileType.VIDEO);
            fileCategoryMap.put(ext, FileCategory.VIDEO);
        }
    }

    // 根据文件扩展名返回文件类型（type）
    public static int getFileType(String fileName) {
        String extension = getFileExtension(fileName);
        FileType fileType = fileTypeMap.getOrDefault(extension, FileType.OTHER);

        switch (fileType) {
            case FOLDER:
                return 0; // 文件夹
            case VIDEO:
                return 1; // 视频
            case AUDIO:
                return 2; // 音频
            case CODE:
                return 3; // 代码
            case PDF:
                return 4; // PDF
            case EXCEL:
                return 5; // Excel
            case TXT:
                return 6; // txt
            case DOC:
                return 7; // docx/doc
            case IMAGE:
                return 8; // 图片
            case ZIP:
                return 9; // zip
            default:
                return 10; // 其他
        }
    }

    // 根据文件扩展名返回文件类别（category）
    public static int getFileCategory(String fileName) {
        String extension = getFileExtension(fileName);
        FileCategory fileCategory = fileCategoryMap.getOrDefault(extension, FileCategory.OTHER);

        switch (fileCategory) {
            case DOCUMENT:
                return 0; // 文档
            case VIDEO:
                return 1; // 视频
            case AUDIO:
                return 2; // 音频
            case IMAGE:
                return 3; // 图片
            default:
                return 4; // 其他
        }
    }

    // 辅助函数：获取文件扩展名
    private static String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // 无扩展名
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }
}
