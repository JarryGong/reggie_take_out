package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.SetmealDto;
import org.example.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 更新菜品售卖状态
     */
    void updateSetmealStatus(Integer updateStatus,String[] ids);

    /**
     * 新增套餐,同时需要保存套餐和相关联的菜品
     * @param setmealDto
     * @return
     */
    void saveSetmealWithDish(SetmealDto setmealDto);

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<SetmealDto> page(int page, int pageSize, String name);

    /**
     * 删除或者批量删除套餐，同时删除相关联的菜品信息
     * @param ids
     * @return
     */
    void deleteSetmealByIdWithDish(String[] ids);

    /**
     * 修改套餐页面
     * 根据套餐id查询套餐信息及相关联的菜品信息
     * @param setmealId
     */
    SetmealDto getBysetmealIdWithDish(Long setmealId);


    /**
     * 修改套餐的同时更新相关联的菜品表
     * @param setmealDto
     */
    void updateSetmealByIdWithDish(SetmealDto setmealDto);
}
