package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.CateListDTO;
import com.Xxy.domain.entity.Article;
import com.Xxy.domain.entity.Category;
import com.Xxy.domain.entity.User;
import com.Xxy.domain.vo.CategoryVo;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.domain.vo.UserListVo;
import com.Xxy.mapper.CategoryMapper;
import com.Xxy.service.ArticleService;
import com.Xxy.service.CategoryService;
import com.Xxy.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2024-06-15 15:21:22
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
        //查询文章表状态为已发布的文章
        LambdaQueryWrapper<Article> articleWrapper=new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articleList = articleService.list(articleWrapper);
        //获取文章的分类id并且去重,这里使用的也是stream流
        Set<Long> categoryIds = articleList.stream()
                .map(article -> article.getCategoryId())
                //转化成set相当于直接去重了
                .collect(Collectors.toSet());
        //查询分类表
        List<Category> categories = listByIds(categoryIds);
        categories = categories.stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);

        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public List<CategoryVo> listAllCategory() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, SystemConstants.NORMAL);
        List<Category> list = list(wrapper);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(list, CategoryVo.class);
        return categoryVos;
    }

    @Override
    public ResponseResult getCategory(int pageNum, int pageSize, CateListDTO cateListDTO) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(cateListDTO.getStatus()), Category::getStatus, cateListDTO.getStatus());
        //对articleId进行判断
        queryWrapper.like(StringUtils.hasText(cateListDTO.getName()), Category::getName, cateListDTO.getName());
        //分页查询
        Page<Category> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Category> categoryList = list(queryWrapper);
        List<CategoryVo> categoryVoList = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        PageVo pageVo = new PageVo(categoryVoList, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }
}

