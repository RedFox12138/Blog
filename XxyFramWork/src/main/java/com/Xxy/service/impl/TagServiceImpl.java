package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.TagListDto;
import com.Xxy.domain.entity.Category;
import com.Xxy.domain.entity.Tag;
import com.Xxy.domain.vo.CategoryVo;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.domain.vo.TagVo;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.mapper.TagMapper;
import com.Xxy.service.TagService;
import com.Xxy.utils.BeanCopyUtils;
import com.Xxy.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2024-06-22 15:40:31
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页查询
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(tagListDto.getName()), Tag::getName, tagListDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()), Tag::getRemark, tagListDto.getRemark());
        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult<PageVo> AddTag(TagListDto tagListDto) {

        if (!StringUtils.hasText(tagListDto.getName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.TAGNAMEEMPTY);
        }

        // 判空并设置remark
        if (!StringUtils.hasText(tagListDto.getRemark())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.TAGREMARKEMPTY);
        }

        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getName, tagListDto.getName());

        List<Tag> existingTags = tagMapper.selectList(queryWrapper);

        Tag tag;

        if (existingTags.isEmpty()) {
            // 如果不存在，创建新Tag
            tag = new Tag();
            tag.setCreateTime(new Date()); // 设置当前时间为创建时间
            tag.setCreateBy(SecurityUtils.getLoginUser().getUser().getId()); // 替换为实际的创建人，比如从当前会话中获取用户信息
        } else {
            // 如果存在，进行更新操作
            tag = existingTags.get(0);
        }
        // 设置或更新字段
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());
        tag.setUpdateTime(new Date()); // 设置当前时间为更新时间
        int result;
        if (existingTags.isEmpty()) {
            // 插入数据到数据库
            result = tagMapper.insert(tag);
            if (result > 0) {
                return ResponseResult.okResult("Tag added successfully");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.TAGINSERTFIAL);
            }
        } else {
            // 更新数据到数据库
            result = tagMapper.updateById(tag);
            if (result > 0) {
                return ResponseResult.okResult("Tag updated successfully");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.TAGINSERTFIAL);
            }
        }
    }

    @Override
    public ResponseResult DeleteTag(Long id) {
        Tag tag = tagMapper.selectById(id);

        int result = tagMapper.deleteById(id);
        if (result > 0) {
            return ResponseResult.okResult("Tag deleted successfully");
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.TAGDELETEFIAL);
        }
    }

    @Override
    public ResponseResult GetTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.TAGFINDFIAL);
        }
        TagVo tagVos = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVos);
    }

    @Override
    public ResponseResult UpdateTag(TagVo tagVo) {
        Tag tag = tagMapper.selectById(tagVo.getId());
        if (tag == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.TAGFINDFIAL);
        } else {
            // 设置或更新字段
            tag.setName(tagVo.getName());
            tag.setRemark(tagVo.getRemark());
            tag.setUpdateTime(new Date()); // 设置当前时间为更新时间
            tagMapper.updateById(tag);
        }
        return ResponseResult.okResult();
    }

    @Override
    public List<TagVo> listAllTag() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Tag::getId,Tag::getName);
        List<Tag> list = list(wrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(list, TagVo.class);
        return tagVos;
    }
}

