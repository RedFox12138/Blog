package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.AddArticleDto;
import com.Xxy.domain.dto.ArticleListDto;
import com.Xxy.domain.dto.UpdateArticleDto;
import com.Xxy.domain.entity.Article;
import com.Xxy.domain.entity.ArticleTag;
import com.Xxy.domain.entity.Category;
import com.Xxy.domain.vo.*;
import com.Xxy.mapper.ArticleMapper;
import com.Xxy.service.ArticleTagService;
import com.Xxy.service.CategoryService;
import com.Xxy.utils.BeanCopyUtils;
import com.Xxy.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements com.Xxy.service.ArticleService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public ResponseResult hotArticleList() {
        // 查询热门文章，封装到ResponseResult里并返回
        LambdaQueryWrapper<Article>queryWrapper=new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只能查询出十条消息，可以采用分页操作之类的
        Page<Article> page = new Page(1,10);
        page(page,queryWrapper);
        List<Article> articles = page.getRecords();

// 遍历 articles 列表
        for (Article article : articles) {
            // 获取文章的 id
            Long id = article.getId();

            // 从 Redis 中获取 viewCount
            Integer viewCount = redisCache.getCacheMapValue("Article:viewCount", id.toString());

            // 检查是否从 Redis 中成功获取了 viewCount
            if (viewCount != null) {
                // 将 viewCount 设置到 article 对象中
                article.setViewCount(viewCount.longValue());
            } else {
                // 如果没有获取到 viewCount，可以设置一个默认值，例如 0
                article.setViewCount(0L);
            }
        }


        List<HotArticleVo> articleVos = new ArrayList<>();
        //bean拷贝
//        for(Article article:articles){
//            HotArticleVo vo= new HotArticleVo();
//            BeanUtils.copyProperties(articles,vo);
//            articleVos.add(vo);
//        }
        List<HotArticleVo> vs = BeanCopyUtils.copyBeanList(articles,HotArticleVo.class);
        return ResponseResult.okResult(vs);
    }

    @Override
        public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
            //查询条件
            LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            // 如果 有categoryId 就要 查询时要和传入的相同
            lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0 ,Article::getCategoryId,categoryId);
            // 状态是正式发布的
            lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
            // 对isTop进行降序
            lambdaQueryWrapper.orderByDesc(Article::getIsTop);

            //分页查询
            Page<Article> page = new Page<>(pageNum,pageSize);
            page(page,lambdaQueryWrapper);

            List<Article> articles = page.getRecords();
            //查询categoryName
            articles.stream()
                    .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                    .collect(Collectors.toList());
            //articleId去查询articleName进行设置
//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }

            //封装查询结果
            List<articleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), articleListVo.class);

            PageVo pageVo = new PageVo(articleListVos,page.getTotal());
            return ResponseResult.okResult(pageVo);
        }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue("Article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成Vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article,ArticleDetailVo.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        redisCache.incrementCacheMapValue("Article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        //添加 博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);

        //这个流操作，相当于将只含有articleTags的列表，转换成同时存在articleTags的列表和articleId的列表
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        //添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<AdminArticleListVo> pageTagList(int pageNum, int pageSize, ArticleListDto articleListDto) {
        // 查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 状态为正常的文章
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        // 根据title进行模糊查询
        if (articleListDto.getTitle() != null && !articleListDto.getTitle().isEmpty()) {
            lambdaQueryWrapper.like(Article::getTitle, articleListDto.getTitle());
        }
        // 根据summary进行模糊查询
        if (articleListDto.getSummary() != null && !articleListDto.getSummary().isEmpty()) {
            lambdaQueryWrapper.like(Article::getSummary, articleListDto.getSummary());
        }
        // 对isTop进行降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        // 分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);

        //封装查询结果成vo
        List<AdminArticleListVo> adminArticleListVos = BeanCopyUtils.copyBeanList(page.getRecords(),AdminArticleListVo.class);
        //两个参数分别是查到的集合和总个数
        PageVo pageVo = new PageVo(adminArticleListVos,page.getTotal());
        //封装查询结果成vo

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticle(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //转换成Vo
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());
        List<ArticleTag> articleTags = articleTagService.list(articleTagLambdaQueryWrapper);
        List<Long> tags = articleTags.stream().map(articleTag -> articleTag.getTagId()).collect(Collectors.toList());

        AdminArticleListVo articleVo = BeanCopyUtils.copyBean(article,AdminArticleListVo.class);
        articleVo.setTags(tags);
        return ResponseResult.okResult(articleVo);

    }

    @Override
    public ResponseResult updateArticle(UpdateArticleDto updateArticleDto) {
        Article article = BeanCopyUtils.copyBean(updateArticleDto,Article.class);
        updateById(article);
        //删除原有的 标签和博客的关联
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(articleTagLambdaQueryWrapper);
        //添加新的博客和标签的关联信息
        List<ArticleTag> articleTags = updateArticleDto.getTags().stream()
                .map(tagId -> new ArticleTag(updateArticleDto.getId(), tagId))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticle(Long id) {
        articleMapper.deleteById(id);
        return ResponseResult.okResult();
    }


}
