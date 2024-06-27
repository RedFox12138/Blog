package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.AddArticleDto;
import com.Xxy.domain.dto.ArticleListDto;
import com.Xxy.domain.dto.UpdateArticleDto;
import com.Xxy.domain.entity.Article;
import com.Xxy.domain.vo.AdminArticleListVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto article);

    ResponseResult<AdminArticleListVo> pageTagList(int pageNum, int pageSize, ArticleListDto articleListDto);

    ResponseResult getArticle(Long id);

    ResponseResult updateArticle(UpdateArticleDto updateArticleDto);

    ResponseResult deleteArticle(Long id);
}
