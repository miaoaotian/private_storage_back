package com.self_back.controller;

import com.self_back.entity.po.FileInfo;
import com.self_back.entity.po.Share;
import com.self_back.entity.vo.FilesVO;
import com.self_back.entity.vo.UserInfoVo;
import com.self_back.service.FileInfoService;
import com.self_back.service.OutShareService;
import com.self_back.utils.Constant;
import com.self_back.utils.Result;
import com.self_back.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/share")
public class OutShareController {
    @Autowired
    private OutShareService outShareService;
    @Autowired
    private FileInfoService fileInfoService;
    @GetMapping("/{link}")
    public Result<?> getInfoByLink(@PathVariable String link) {
        String newLink = Constant.SHARE_BASE_URL + link;
        Share share = outShareService.getInfoByLink(newLink);
        return Result.success(share);
    }
    @GetMapping("/getUserInfo")
    public Result<?> getUserInfo(@RequestParam("userId") int userId) {
        UserInfoVo userInfoVo = outShareService.getUserInfo(userId);
        return Result.success(userInfoVo);
    }
    @GetMapping("/getauth")
    public Result<?> getAuth(@RequestHeader("Authorization") String token) {
        int userId = TokenUtil.parseToken(token);
        return Result.success(userId);
    }
    @GetMapping("/getFiles")
    public Result<List<FilesVO>> getFiles(@RequestParam("userId") int userId, @RequestParam("fileId") int fileId) {
        List<FilesVO> filesVOS = outShareService.getFileInfoByShareId(fileId, userId);
        return Result.success(filesVOS);
    }
    @PostMapping("/getFileByIdAndCategory")
    public Result<List<FilesVO>> getFileByIdAndCategory(@RequestParam("userId") Integer userId, @RequestParam(value = "fileId",required = false) Integer fileId, @RequestParam(value = "category",required = false) Integer category) {
        List<FilesVO> filesVOS = fileInfoService.getFileInfoByFileIdAndCategory2(userId, fileId, category);
        return Result.success(filesVOS);
    }
    @GetMapping("/getFileInfo")
    public Result<?> getFileInfo(@RequestParam("fileId") int fileId) {
        FileInfo fileInfo = outShareService.getFileInfo(fileId);
        return Result.success(fileInfo);
    }
    @GetMapping("/getPath")
    public Result<List<Map<String, Object>>> getPath(@RequestParam("id") int id) {
        List<Map<String, Object>> paths = fileInfoService.getPath(id);
        Collections.reverse(paths);
        return Result.success(paths);
    }
    @Autowired
    private final ServletContext servletContext;
    public OutShareController(ServletContext servletContext) {
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
    @PostMapping("/changeFolder")
    public Result<?> changeFolder(@RequestHeader("Authorization") String token, @RequestParam("tarId") int tarId,@RequestParam("id") int id) {
        int userId = TokenUtil.parseToken(token);
        outShareService.changeFolder(userId, tarId, id);
        return Result.success();
    }

}
