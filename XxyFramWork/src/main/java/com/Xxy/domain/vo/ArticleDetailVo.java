package com.Xxy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVo {
    private Long id;
    //标题
    private String title;
    //文章摘要
    private String summary;
    private Long categoryId;
    private String content;

    //所属分类id
    private String categoryName;
    //缩略图
    private String thumbnail;
    //访问量
    private Long viewCount;
    private Date createTime;
    private String userName;
    private List<String> tagNameList;

}
