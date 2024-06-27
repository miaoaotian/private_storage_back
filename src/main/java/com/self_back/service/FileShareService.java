package com.self_back.service;

import com.self_back.entity.dto.ShareFileDTO;
import com.self_back.entity.po.Share;
import com.self_back.entity.vo.ShareVO;
import com.self_back.mapper.FileShareMapper;
import com.self_back.utils.Constant;
import com.self_back.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.self_back.utils.StringTools;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileShareService {
    @Autowired
    private FileShareMapper fileShareMapper;
    public Share shareFile(ShareFileDTO shareFileDTO) {
        Share share = new Share();
        share.setFileId(shareFileDTO.getFileId());
        share.setUserId(shareFileDTO.getUserId());
        if (shareFileDTO.getCode() == null || shareFileDTO.getCode().length() == 0) {
            share.setCode((StringTools.getRandomString(4)));
        } else {
            share.setCode(shareFileDTO.getCode());
        }
        share.setValidType(shareFileDTO.getValid_type());
        share.setShareLink(generateShareLink()); // 生成唯一分享链接
        share.setCreateTime(new Date());
        share.setExpireTime(calculateExpiryDate(share.getValidType())); // 计算过期时间

        fileShareMapper.insertShare(share);
        return share;
    }

    private Date calculateExpiryDate(int validType) {
        Calendar calendar = Calendar.getInstance(); // 获取当前日期和时间
        switch (validType) {
            case 0: // 1天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case 1: // 7天
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                break;
            case 2: // 30天
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                break;
            case 3: // 永久有效，假设设置为100年后过期
                calendar.add(Calendar.YEAR, 100);
                break;
            default:
                throw new IllegalArgumentException("无效的valid_type");
        }
        return calendar.getTime();
    }
    public String generateShareLink() {
        // 生成一个唯一的UUID
        String uniqueKey = UUID.randomUUID().toString();
        return Constant.SHARE_BASE_URL + uniqueKey;
    }

    public List<ShareVO> getAllShares(String token) {
        int userId = TokenUtil.parseToken(token);
        return fileShareMapper.getAllShares(userId);
    }

    public void cancelShare(int shareId) {
        fileShareMapper.cancelShare(shareId);
    }
}
