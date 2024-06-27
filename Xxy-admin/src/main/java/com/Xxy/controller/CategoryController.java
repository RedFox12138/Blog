package com.Xxy.controller;

import com.Xxy.annotation.SystemLog;
import com.Xxy.domain.ResponseResult;
import com.Xxy.domain.dto.CateListDTO;
import com.Xxy.domain.entity.Category;
import com.Xxy.domain.vo.CategoryVo;
import com.Xxy.domain.vo.ExcelCategoryVo;
import com.Xxy.enums.AppHttpCodeEnum;
import com.Xxy.service.CategoryService;
import com.Xxy.utils.BeanCopyUtils;
import com.Xxy.utils.WebUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        List<CategoryVo> list = categoryService.listAllCategory();
        return ResponseResult.okResult(list);
    }


    //在之前进行权限认证
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的数据
            List<Category> categoryVos = categoryService.list();

            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);

        } catch (Exception e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    @SystemLog(businessName = "进入分类擦管理页面")//接口描述，用于'日志记录'功能
    @GetMapping("/list")
    public ResponseResult GetCate(int pageNum , int pageSize, CateListDTO cateListDTO)
    {
        return categoryService.getCategory(pageNum,pageSize,cateListDTO);
    }

    @PostMapping
    public ResponseResult add(@RequestBody CateListDTO CateListDTO){
        Category category = BeanCopyUtils.copyBean(CateListDTO, Category.class);
        categoryService.save(category);
        return ResponseResult.okResult();
    }
    @DeleteMapping(value = "/{id}")
    public ResponseResult remove(@PathVariable(value = "id")Long id){
        categoryService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping(value = "/{id}")
    //①根据分类的id来查询分类
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        Category category = categoryService.getById(id);
        return ResponseResult.okResult(category);
    }

    @PutMapping
    //②根据分类的id来修改分类
    public ResponseResult edit(@RequestBody Category category){
        categoryService.updateById(category);
        return ResponseResult.okResult();
    }
}