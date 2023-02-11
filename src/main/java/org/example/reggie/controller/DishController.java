package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.DishFlavor;
import org.example.reggie.service.CategoryService;
import org.example.reggie.service.DishFlavorService;
import org.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
@Transactional
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        //将关于dish的字段存放在dish表中，SpringBoot会先雪花算法生成id在进行相关操作
        dishService.save(dishDto);
        log.info(dishDto.toString());
        //将dishDto中的自动生成的主键id取出
        Long id = dishDto.getId();
        //存放口味数据
        List<DishFlavor> flavorList = dishDto.getFlavors().stream().map(dishFlavor -> {
            //填充口味表中的dishId字段
            dishFlavor.setDishId(id);
            return dishFlavor;
        }).collect(Collectors.toList());

        //第一种方法
        /*for (DishFlavor dishFlavor:list) {
            //保存在dishFlavor表
            dishFlavorService.save(dishFlavor);
        }*/
        //第二中方法
        dishFlavorService.saveBatch(flavorList, flavorList.size());
        return R.success("添加菜品成功");
    }
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public R<Page<DishDto>> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam(defaultValue = "",value = "name") String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页插件
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件查询包装类
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name !=null,Dish::getName,name);
        //添加排序条件，根据Sort字段排序
        queryWrapper.orderByAsc(Dish::getSort);
        //分页查询
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝(拷贝属性)
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //records中存放的是返回给前台的Dish实体数据
        List<Dish> records = pageInfo.getRecords();
        //创建存放DishDto属性的List
        List<DishDto> list = new ArrayList<>();
        //第一种方法
        /*for (Dish dish:records) {
            //每次循环创建一个对象，存放到list
            DishDto dishDto = new DishDto();
            //将dish中的数据拷贝到每一次创建的dishDto中
            BeanUtils.copyProperties(dish,dishDto);
            //获取dish中的categoryId
            Long categoryId = dish.getCategoryId();
            //通过categoryId查询Category分类对象
            Category category = categoryService.getById(categoryId);
            //获取分类名
            String categoryName = category.getName();
            //将获取的categoryName存放到dishDto对象中去
            dishDto.setCategoryName(categoryName);

            list.add(dishDto);
        }*/
        //第二种方法
        list = records.stream().map(dish -> {
            //每次循环创建一个对象，存放到list
            DishDto dishDto = new DishDto();
            //将dish中的数据拷贝到每一次创建的dishDto中
            BeanUtils.copyProperties(dish,dishDto);
            //获取dish中的categoryId
            Long categoryId = dish.getCategoryId();
            //通过categoryId查询Category分类对象
            Category category = categoryService.getById(categoryId);
            //获取分类名
            String categoryName = category.getName();
            //将获取的categoryName存放到dishDto对象中去
            dishDto.setCategoryName(categoryName);
            //返回
            return dishDto;
        }).collect(Collectors.toList());

        //存放新数据的list添加到dishDtoPage中
        dishDtoPage.setRecords(list);
        //返回成功信息
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     */
    @GetMapping("/{id}")
    public R<DishDto> display(@PathVariable("id")Long id){
        log.info(id.toString());
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
}
