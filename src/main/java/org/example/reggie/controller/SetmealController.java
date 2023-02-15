package org.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.R;
import org.example.reggie.dto.SetmealDto;
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
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> Page(int page, int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //查询分页
        Page<SetmealDto> setmealDtoPage = setmealService.page(page, pageSize, name);
        return  R.success(setmealDtoPage);
    }
}
