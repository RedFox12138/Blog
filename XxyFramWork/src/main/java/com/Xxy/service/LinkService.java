package com.Xxy.service;

import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.LinkListDTO;
import com.Xxy.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2024-06-16 11:22:04
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult linkList(int pageNum, int pageSize, LinkListDTO linkListDTO);
}

