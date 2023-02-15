package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    void saveByIdWithFlavor(DishDto dishDto);

    /**菜品修改页面
     * 根据id查询回显菜品信息和口味信息
     * @param id
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品的同时更新口味表
     * @param dishDto
     * @return
     */
    void updateByIdWithFlavor(DishDto dishDto);

    /**
     * 更新菜品售卖状态
     */
    void updateDishStatus(Integer updateStatus,String[] ids);

    /**
     * 根据id删除菜品和对应口味
     *
     * @param ids
     * @return
     */
    void deleteByIdWithFlavor(String[] ids);
}
