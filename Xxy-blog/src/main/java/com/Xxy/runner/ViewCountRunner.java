package com.Xxy.runner;

import com.Xxy.domain.entity.Article;
import com.Xxy.mapper.ArticleMapper;
import com.Xxy.utils.RedisCache;
import io.swagger.models.auth.In;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ViewCountRunner implements CommandLineRunner {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public void run(String... args) throws Exception {
        //查询博客信息 主要是id viewcount
        List<Article> articles = articleMapper.selectList(null);
        Map<String,Integer> viewCountMap = articles.stream()
                .collect(Collectors.toMap(article -> article.getId().toString(), article -> article.getViewCount().intValue()));
        //存储到redis中
        redisCache.setCacheMap("Article:viewCount",viewCountMap);
    }
}
