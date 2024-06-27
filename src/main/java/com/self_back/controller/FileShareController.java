package com.self_back.controller;

import com.self_back.entity.dto.ShareFileDTO;
import com.self_back.entity.vo.ShareVO;
import com.self_back.service.FileShareService;
import com.self_back.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class FileShareController {
    @Autowired
    private FileShareService fileShareService;
    @PostMapping("/shareFile")
    public Result<?> shareFile(ShareFileDTO shareFileDTO) {

        return Result.success(fileShareService.shareFile(shareFileDTO));
    }
    @GetMapping("/getAllShares")
    public Result<List<ShareVO>> getAllShares(@RequestHeader("Authorization") String token) {
        return Result.success(fileShareService.getAllShares(token));
    }
    @DeleteMapping("/cancelShare")
    public Result<?> cancelShare(@RequestParam("shareId") int shareId) {
        fileShareService.cancelShare(shareId);
        return Result.success();
    }
}
