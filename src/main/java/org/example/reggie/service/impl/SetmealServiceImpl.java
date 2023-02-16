package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.entity.SetmealDish;
import org.example.reggie.mapper.SetmealMapper;
import org.example.reggie.service.CategoryService;
import org.example.reggie.service.SetmealDishService;
import org.example.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealService setmealService;

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private CategoryService categoryService;

    /**
     * 更新套餐售卖状态
     *
     * @param updateStatus
     * @param ids
     */
    @Override
    public void updateSetmealStatus(Integer updateStatus, String[] ids) {
        List<Setmeal> setmeals = Arrays.stream(ids).map(s -> {
            //将字符串转换为长整型
            Long id = Long.parseLong(s);
            //构造Setmeal实例
            Setmeal setmeal = new Setmeal();
            //设置id
            setmeal.setId(id);
            //设置售卖状态
            setmeal.setStatus(updateStatus);
            //返回
            return setmeal;
        }).collect(Collectors.toList());

        //更新状态
        setmealService.updateBatchById(setmeals, setmeals.size());
    }

    /**
     * 新增套餐,同时需要保存套餐和相关联的菜品
     *
     * @param setmealDto
     * @return
     */
    @Override
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表，执行insert操作
        setmealService.save(setmealDto);//自动向上转型
        //构造集合存放与套餐关联的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //循环处理菜品信息数据,处理之后存放到list
        List<SetmealDish> list = setmealDishes.stream().map(setmealDish -> {
            //将上方生成的套餐id赋值给setmealDish的setmealId
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        //保存套餐相关联的菜品信息，操作setmeal dish 表，执行insert操作
        setmealDishService.saveBatch(list);
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        //构造分页插件
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造条件查询包装类
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据创建时间字段排序
        queryWrapper.orderByAsc(Setmeal::getCreateTime).orderByAsc(Setmeal::getStatus).orderByDesc(Setmeal::getUpdateTime);
        //分页查询Setmeal表
        setmealService.page(setmealPage, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        //setmeals中存放的是返回给前台的Setmeal实体数据
        List<Setmeal> setmeals = setmealPage.getRecords();
        //创建存放SetmealDto的List
        List<SetmealDto> setmealDtos = setmeals.stream().map(setmeal -> {
            //每次循环是创建存放进集合
            SetmealDto setmealDto = new SetmealDto();
            //将Setmeal的数据拷贝到SetmealDto中
            BeanUtils.copyProperties(setmeal, setmealDto);
            //获取setmeal中的categoryId
            Long categoryId = setmeal.getCategoryId();
            //通过categoryId查询Category分类对象
            Category category = categoryService.getById(categoryId);
            //获取分类名
            String categoryName = category.getName();
            //将获取的categoryName存放到setmealDto对象中去
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        //存放新数据的setmealDtos添加到setmealDtoPage中
        setmealDtoPage.setRecords(setmealDtos);
        return setmealDtoPage;
    }

    /**
     * 删除或者批量删除套餐，同时删除相关联的菜品信息
     *
     * @param ids
     * @return
     */
    @Override
    public void deleteSetmealByIdWithDish(String[] ids) {

        for (String id:ids) {
            //删除基本套餐信息
            setmealService.removeById(id);

            //构造条件查询包装类
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            //构造条件（根据套餐id删除关联菜品）
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            //删除套餐相关联的菜品
            setmealDishService.remove(queryWrapper);
        }
    }

    /**
     * 修改套餐页面
     * 根据套餐id查询套餐信息及相关联的菜品信息
     *
     * @param setmealId
     */
    @Override
    public SetmealDto getBysetmealIdWithDish(Long setmealId) {
        //查询套餐基本信息
        Setmeal setmeal = setmealService.getById(setmealId);
        //构造setmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        //对象拷贝
        BeanUtils.copyProperties(setmeal, setmealDto);

        //构造条件查询包装类
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件
        queryWrapper.eq(setmealId != null, SetmealDish::getSetmealId, setmealId);
        //查询相关联菜品信息
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        //将查询到的关联菜品信息存放进setmealDto中
        setmealDto.setSetmealDishes(setmealDishes);
        //返回查询到的关联菜品数据
        return setmealDto;
    }

    /**
     * 修改套餐的同时更新相关联的菜品表
     *
     * @param setmealDto
     */
    @Override
    public void updateSetmealByIdWithDish(SetmealDto setmealDto) {
        //更新套餐表基本信息,自动向上转型
        setmealService.updateById(setmealDto);

        //构造条件查询包装类
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件
        queryWrapper.eq(setmealDto.getId() != null, SetmealDish::getSetmealId, setmealDto.getId());
        //先删除套餐关联的菜品信息
        setmealDishService.remove(queryWrapper);
        //处理从setmealDto获取到菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map(setmealDish -> {
            //setmealId不是主键，不会自动生成，给setmealId手动赋值
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        //添加前台传来的套餐关联菜品信息
        if (setmealDishes.size() != 0) {
            setmealDishService.saveBatch(setmealDishes, setmealDishes.size());
        }

    }
}
