package com.Xxy.controller;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.TagListDto;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.domain.vo.TagVo;
import com.Xxy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
    }

    @PostMapping("")
    public ResponseResult AddTag(@RequestBody TagListDto tagListDto){
        return tagService.AddTag(tagListDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult DeleteTag(@PathVariable("id") Long id) {
        return tagService.DeleteTag(id);
    }

    @GetMapping("/{id}")
    public ResponseResult GetTag(@PathVariable("id") Long id) {
        return tagService.GetTag(id);
    }

    @PutMapping("")
    public ResponseResult UpdateTag(@RequestBody TagVo tagVo) {
        return tagService.UpdateTag(tagVo);
    }


    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        List<TagVo> list = tagService.listAllTag();
        return ResponseResult.okResult(list);
    }
}
