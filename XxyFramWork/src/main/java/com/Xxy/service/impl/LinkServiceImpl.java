package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.LinkListDTO;
import com.Xxy.domain.entity.Category;
import com.Xxy.domain.entity.Link;
import com.Xxy.domain.vo.CategoryVo;
import com.Xxy.domain.vo.LinkVo;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.mapper.LinkMapper;
import com.Xxy.service.LinkService;
import com.Xxy.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2024-06-16 11:22:05
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {


    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的友链
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);
        //转化成Vo
        List<LinkVo>linkVos= BeanCopyUtils.copyBeanList(links, LinkVo.class);

        //封装返回
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult linkList(int pageNum, int pageSize, LinkListDTO linkListDTO) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(linkListDTO.getStatus()), Link::getStatus, linkListDTO.getStatus());
        //对articleId进行判断
        queryWrapper.like(StringUtils.hasText(linkListDTO.getName()), Link::getName, linkListDTO.getName());
        //分页查询
        Page<Link> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Link> linkList = list(queryWrapper);
        List<Link> links = BeanCopyUtils.copyBeanList(linkList, Link.class);
        PageVo pageVo = new PageVo(links, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }
}

