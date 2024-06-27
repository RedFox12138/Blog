package com.Xxy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CateListDTO {
    //状态0:正常,1禁用
    private String status;
    private String name;
    private String description;
}
