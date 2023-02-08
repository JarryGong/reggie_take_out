package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.entity.Category;
import org.example.reggie.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController{

    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addDishAndPackageCategory(@RequestBody Category category){
        log.info("新增分类 分类信息 {}",category.toString());
        //添加菜品或套餐分类
        categoryService.save(category);
        return category.getType()==1 ? R.success("添加菜品分类成功"):R.success("添加套餐分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public R<Page> page(int page,int pageSize){
        log.info("page = {},pageSize = {}",page,pageSize);
        //构造分页构造器
        Page pageInfo = new Page();
        //构造条件查询包装类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据Sort字段排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行分页查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除菜品或套餐分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long ids){
        //获取分类ids
        //Long ids = Long.valueOf(request.getParameter("ids"));
        log.info("菜品或套餐分类id: {}",ids);
        //执行删除
        categoryService.remove(ids);
        return R.success("删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("更新修改原始信息 {}",category.toString());
        //执行更新查询
        categoryService.updateById(category);
        return R.success("更新成功");
    }
}
