package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @Override
    public void saveByIdWithFlavor(DishDto dishDto) {
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
    }

    /**菜品修改页面
     * 根据id查询回显菜品信息和口味信息
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

    /**
     * 修改菜品的同时更新口味表
     * @param dishDto
     * @return
     */
    @Override
    public void updateByIdWithFlavor(DishDto dishDto) {
        //更新菜品表基本信息,自动向上转型
        dishService.updateById(dishDto);

        //构造条件查询包装类
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        //清理当前菜品口味数据
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors().stream().map(dishFlavor -> {
            //dishId不是主键，不会自动生成，给dishId手动赋值
            dishFlavor.setDishId(dishDto.getId());
            return dishFlavor;
        }).collect(Collectors.toList());
        if (flavors.size()!=0) {
            dishFlavorService.saveBatch(flavors, flavors.size());
        }
    }

    /**
     * 修改售卖状态
     * @param updateStatus
     * @param ids
     * @return
     */
    @Override
    public void updateDishStatus(Integer updateStatus, String[] ids) {

        /*for (String id:ids) {
            //创建实体
            Dish dish = new Dish();

            //将id和修改的状态代码放进实体中
            dish.setId(Long.valueOf(id));
            //更新售卖状态
            dish.setStatus(updateStatus);
            //修改售卖状态
            dishService.updateById(dish);
        }*/

        List<Dish> dishes = Arrays.stream(ids).map(s -> {
            Long id = Long.parseLong(s);
            //创建实体
            Dish dish = new Dish();

            //将id和修改的状态代码放进实体中
            dish.setId(id);
            //设置售卖状态
            dish.setStatus(updateStatus);
            return dish;
        }).collect(Collectors.toList());
        //更新售卖状态
        dishService.updateBatchById(dishes,dishes.size());
    }

    /**
     * 根据id删除菜品和对应口味
     * @param ids
     * @return
     */
    @Override
    public void deleteByIdWithFlavor(String[] ids) {
        for (String id:ids) {
            //删除菜品
            dishService.removeById(id);

            //构造条件查询包装类
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            //构造条件（根据dishId删除）
            queryWrapper.eq(id!=null,DishFlavor::getDishId, id);
            //删除菜品对应口味
            dishFlavorService.remove(queryWrapper);
        }

    }
}
