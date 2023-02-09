package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.entity.Dish;
import org.example.reggie.service.DishService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @GetMapping("page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize){
        log.info("page = {},pageSize = {}",page,pageSize);
        //构造分页插件
        Page pageInfo = new Page<>(page,pageSize);
        //构造条件查询包装类
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据Sort字段排序
        queryWrapper.orderByAsc(Dish::getSort);
        //分页查询
        dishService.page(pageInfo, queryWrapper);
        //返回成功信息
        return R.success(pageInfo);
    }
}
