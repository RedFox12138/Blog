package com.Xxy.domain.vo;

import com.Xxy.domain.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterVo {
    private List<Menu> menus;
}
