package com.self_back.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo {
    private String username;
    private String avatar;
    private Long useSpace;
    private Long totalSpace;
}
