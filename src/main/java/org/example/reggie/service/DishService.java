package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.dto.DishDto;
import org.example.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     */
    DishDto getByIdWithFlavor(Long id);
}
