package com.Xxy.service.impl;

import com.Xxy.constants.SystemConstants;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.entity.Comment;
import com.Xxy.domain.vo.CommentVo;
import com.Xxy.domain.vo.PageVo;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.exception.SystemException;
import com.Xxy.mapper.CommentMapper;
import com.Xxy.service.CommentService;
import com.Xxy.service.UserService;
import com.Xxy.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2024-06-19 16:41:29
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId, articleId);
        //根评论的rootId为-1，来判断一个评论是不是根评论
        queryWrapper.eq(Comment::getRootId, -1);
        //评论类型
        queryWrapper.eq(Comment::getType,commentType);


        //分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        //拿目标评论的userid来查它的名字，拿创建人来查创建人的名字
        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());
        //查询所有根评论对应的子评论集合，并且赋值给对应的属性
        for (CommentVo commentVo : commentVoList) {
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            commentVo.setChildren(children);
        }

        return ResponseResult.okResult(new PageVo(commentVoList, page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空，但是这个功能在前端已经有校验了
        if(!StringUtils.hasText(comment.getContent()))
        {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
            //创建人、创建时间需要进行手动赋值，因为json请求里没有
        //发表评论需要token，防止刷评论
        //save函数自己会调用insert语句进行添加，并且我们在entity里已经加入了自动填充的注解
        //mybatisplus会帮我们自动填充
        save(comment);
        return ResponseResult.okResult();
    }


    /**
     * 根据根评论的id查询所对应的子评论的集合
     * @param id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(comments);
        return commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> list) {
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历vo集合
        //通过creatBy查询用户的昵称并幅值
        for (CommentVo commentVo : commentVos) {
            String nikename = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nikename);
            //通过toCommentUserId查询用户的昵称并幅值
            //如果toCommentUserId不为-1才进行查询
            if (commentVo.getToCommentId() != -1 ) {
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVos;
    }
}

