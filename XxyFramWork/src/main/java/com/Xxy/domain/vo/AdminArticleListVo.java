package com.Xxy.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminArticleListVo {
    private Long id;
    //文章内容
    private String content;

    private Date createTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
    //所属分类id
    private Long categoryId;
    //是否允许评论 1是，0否
    private String isComment;
    //是否置顶（0否，1是）
    private String isTop;
    //状态（0已发布，1草稿）
    private String status;
    //标题
    private String title;
    //文章摘要
    private String summary;
    //缩略图
    private String thumbnail;
    //访问量
    private Long viewCount;
    //
    private List<Long> tags;




}
