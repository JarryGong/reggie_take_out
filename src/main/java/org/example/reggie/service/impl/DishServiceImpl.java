package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.DishFlavor;
import org.example.reggie.mapper.DishMapper;
import org.example.reggie.service.DishFlavorService;
import org.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，通过id从dish表查询
        Dish dish = dishService.getById(id);
        //创建dishDto对象
        DishDto dishDto = new DishDto();
        //对象拷贝
        BeanUtils.copyProperties(dish,dishDto);
        //构造条件擦查询包装类
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件
        queryWrapper.eq(DishFlavor::getDishId, id);
        //查询口味信息，通过dishId从dishFlavor表查询
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //将查询到的口味信息集合赋值到dishDto对象中的flavor属性当中
        dishDto.setFlavors(flavors);
        //返回处理的数据对象
        return dishDto;
    }
}
