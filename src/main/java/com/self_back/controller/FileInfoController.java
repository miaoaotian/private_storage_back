package com.self_back.controller;

import com.self_back.entity.dto.FileUploadDTO;
import com.self_back.entity.vo.FilesVO;
import com.self_back.service.FileInfoService;
import com.self_back.utils.Constant;
import com.self_back.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FileInfoController {
    @Autowired
    FileInfoService fileInfoService;

    @PostMapping("/addFolder")
    public Result<?> addFolder(@RequestHeader("Authorization") String token, @RequestParam("folderName") String folderName, @RequestParam("pid") int pid) {
        fileInfoService.addFolder(token, folderName, pid);
        return Result.success();
    }
    @PostMapping("changeName")
    public Result<?> changeName( @RequestParam("fileId") int fileId, @RequestParam("newName") String newName) {
        fileInfoService.changeFileName(fileId, newName);
        return Result.success();
    }
    @PostMapping("deleteFile")
    public Result<?> deleteFile(@RequestParam("ids") int[] ids) {
        log.info("deleteFile: " + ids);
        fileInfoService.deleteFile(ids);
        return Result.success();
    }
    @PostMapping("deleteFile2")
    public Result<?> deleteFile2(@RequestParam("ids") int[] ids) {
        log.info("deleteFile: " + ids);
        fileInfoService.deleteFile2(ids);
        return Result.success();
    }
    @PostMapping("refresh")
    public Result<?> refresh(@RequestParam("ids") int[] ids) {
        fileInfoService.refresh(ids);
        return Result.success();
    }
    @PostMapping("/getFileByIdAndCategory")
    public Result<List<FilesVO>> getFileByIdAndCategory(@RequestHeader("Authorization") String token, @RequestParam(value = "fileId",required = false) Integer fileId, @RequestParam(value = "category",required = false) Integer category) {
        List<FilesVO> filesVOS = fileInfoService.getFileInfoByFileIdAndCategory(token, fileId, category);
        return Result.success(filesVOS);
    }
    @GetMapping ("/upload")
    public boolean uploadFile(@RequestParam("identifier") String identifier, @RequestParam("chunkNumber") int chunkNumber) {
        String chunkpath = Constant.FILE_PATH + identifier + "_" + chunkNumber;
        File file = new File(chunkpath);
        return file.exists();
    }
    @PostMapping("/upload")
    public Result<?> uploadFile(@RequestHeader("Authorization") String token,FileUploadDTO fileUploadDTO) {
        fileInfoService.upLoadFile(token, fileUploadDTO);
        return Result.success();
    }
    @Autowired
    private final ServletContext servletContext;
    public FileInfoController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") int id) throws MalformedURLException, UnsupportedEncodingException {
        String filePath = fileInfoService.searchPathById(id);
        Path path = Paths.get(filePath).normalize();
        Resource resource = new UrlResource(path.toUri());
        String contentType = servletContext.getMimeType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String filename = resource.getFilename(); // 假设文件名包含中文
        // 对文件名进行URL编码
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .headers(headers)
                .body(resource);
    }
    @GetMapping("/getPath")
    public Result<List<Map<String, Object>>> getPath(@RequestParam("id") int id) {
        List<Map<String, Object>> paths = fileInfoService.getPath(id);
        Collections.reverse(paths);
        return Result.success(paths);
    }

    @GetMapping("/getFileInRecycle")
    public Result<List<FilesVO>> getFileInRecycle(@RequestHeader("Authorization") String token) {
        List<FilesVO> filesVOS = fileInfoService.getFileInRecycle(token);
        return Result.success(filesVOS);
    }
    @GetMapping("/getAllFoldersById")
    public Result<?> getAllFoldersById(@RequestHeader("Authorization") String token, @RequestParam("fileId") int fileId) {
        List<FilesVO> filesVOS = fileInfoService.getAllFoldersById(token,fileId);
        log.info("getAllFoldersById: " + filesVOS);
        return Result.success(filesVOS);
    }
    @PostMapping("/changeFolder")
    public Result<?> changeFolder(@RequestParam("tarId") int tarId, @RequestParam("ids") int[] ids) {
        fileInfoService.changeFolder(tarId, ids);
        return Result.success();
    }
}
