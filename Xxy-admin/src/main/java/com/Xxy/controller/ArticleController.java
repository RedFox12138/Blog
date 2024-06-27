package com.Xxy.controller;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.AddArticleDto;
import com.Xxy.domain.dto.ArticleListDto;
import com.Xxy.domain.dto.UpdateArticleDto;
import com.Xxy.domain.entity.Menu;
import com.Xxy.domain.vo.AdminArticleListVo;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.service.ArticleService;
import com.Xxy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private MenuService menuService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article){
        return articleService.add(article);
    }

    @GetMapping("/list")
    public ResponseResult<AdminArticleListVo> ArticleList(int pageNum, int pageSize, ArticleListDto articleListDto)
    {
        return articleService.pageTagList(pageNum,pageSize,articleListDto);
    }

    @GetMapping("/{id}")
    public ResponseResult GetArticle(@PathVariable("id") Long id)
    {
        return articleService.getArticle(id);
    }

    @PutMapping
    public ResponseResult UpdateArticle(@RequestBody UpdateArticleDto updateArticleDto)
    {
        return articleService.updateArticle(updateArticleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteArticle(@PathVariable("id") Long id)
    {
        return articleService.deleteArticle(id);
    }



}