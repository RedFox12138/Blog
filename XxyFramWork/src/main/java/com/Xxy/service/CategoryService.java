package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.CateListDTO;
import com.Xxy.domain.entity.Category;
import com.Xxy.domain.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2024-06-15 15:21:21
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();

    ResponseResult getCategory(int pageNum, int pageSize, CateListDTO cateListDTO);
}

