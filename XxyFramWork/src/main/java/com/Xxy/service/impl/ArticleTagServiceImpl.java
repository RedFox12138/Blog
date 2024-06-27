package com.Xxy.service.impl;

import com.Xxy.domain.entity.ArticleTag;
import com.Xxy.mapper.ArticleTagMapper;
import com.Xxy.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文章标签关联表(ArticleTag)表服务实现类
 *
 * @author makejava
 * @since 2024-06-23 17:14:11
 */
@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

}

