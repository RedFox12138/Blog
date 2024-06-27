package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.TagListDto;
import com.Xxy.domain.entity.Tag;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.domain.vo.TagVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2024-06-22 15:40:31
 */
public interface TagService extends IService<Tag> {

    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult<PageVo> AddTag(TagListDto tagListDto);

    ResponseResult DeleteTag(Long id);

    ResponseResult GetTag(Long id);

    ResponseResult UpdateTag(TagVo tagVo);

    List<TagVo> listAllTag();
}

