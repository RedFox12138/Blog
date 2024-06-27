package com.Xxy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    //角色名称
    private String roleName;
    //角色状态
    private String status;
    //角色id
    private Long roleid;
}
