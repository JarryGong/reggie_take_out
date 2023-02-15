package org.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Category;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.mapper.SetmealMapper;
import org.example.reggie.service.CategoryService;
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
    private CategoryService categoryService;

    /**
     * 更新套餐售卖状态
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
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        //构造分页插件
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造条件查询包装类
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name !=null, Setmeal::getName,name);
        //添加排序条件，根据创建时间字段排序
        queryWrapper.orderByAsc(Setmeal::getCreateTime).orderByAsc(Setmeal::getStatus).orderByDesc(Setmeal::getUpdateTime);;
        //分页查询Setmeal表
        setmealService.page(setmealPage, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        //setmeals中存放的是返回给前台的Setmeal实体数据
        List<Setmeal> setmeals = setmealPage.getRecords();
        //创建存放SetmealDto的List
        List<SetmealDto> setmealDtos = setmeals.stream().map(setmeal -> {
            //每次循环是创建存放进集合
            SetmealDto setmealDto = new SetmealDto();
            //将Setmeal的数据拷贝到SetmealDto中
            BeanUtils.copyProperties(setmeal,setmealDto);
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
}
