package org.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.service.SetmealDishService;
import org.example.reggie.service.SetmealService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
@Transactional
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 修改售卖状态
     * @param updateStatus
     * @param ids
     * @return
     */
    @PostMapping("/status/{updateStatus}")
    public R<String> updateSetmealStatus(@PathVariable Integer updateStatus, @RequestParam String[] ids){
        log.info(String.valueOf(updateStatus), Arrays.toString(ids));
        //更新
        setmealService.updateSetmealStatus(updateStatus,ids);
        //返回
        return updateStatus == 0 ? R.success("停售成功") : R.success("启售成功");
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息 {}",setmealDto.toString());
        //新增
        setmealService.saveSetmealWithDish(setmealDto);
        //返回
        return R.success("新增套餐成功");
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> Page(int page, int pageSize,String name){
        log.info("分页信息 page = {},pageSize = {},name = {}",page,pageSize,name);
        //查询分页
        Page<SetmealDto> setmealDtoPage = setmealService.page(page, pageSize, name);
        return  R.success(setmealDtoPage);
    }

    /**
     * 修改套餐的同时更新相关联的菜品表
     * @param setmealDto
     */
    @PutMapping
    public R<String> updateSetmealById(@RequestBody SetmealDto setmealDto){
        log.info("套餐修改信息 {}",setmealDto.toString());
        //调用方法
        setmealService.updateSetmealByIdWithDish(setmealDto);
        //返回结果
        return R.success("修改套餐成功");
    }

    /**
     * 删除或者批量删除套餐，同时删除相关联的菜品信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmealById(@RequestParam String[] ids){
        log.info("套餐id {}", Arrays.toString(ids));
        //删除
        setmealService.deleteSetmealByIdWithDish(ids);
        return ids.length>1?R.success("删除套餐成功"):R.success("批量删除套餐成功");
    }

    /**
     * 修改套餐页面
     * 根据套餐id查询套餐信息及相关联的菜品信息
     * @param setmealId
     */
    @GetMapping("/{setmealId}")
    public R<SetmealDto> display(@PathVariable("setmealId") Long setmealId){
        log.info("套餐id {}",setmealId);
        //调用方法通过setmealId查询的套餐及菜品信息封装到setmealDto
        SetmealDto setmealDto = setmealService.getBysetmealIdWithDish(setmealId);
        //返回数据
        return R.success(setmealDto);
    }
}
