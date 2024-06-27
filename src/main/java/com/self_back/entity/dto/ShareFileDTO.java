package com.self_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ShareFileDTO {
    private int fileId;
    private int userId;
    private String code;
    private int valid_type;
}
