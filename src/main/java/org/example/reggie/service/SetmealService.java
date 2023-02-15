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
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<SetmealDto> page(int page, int pageSize, String name);
}
