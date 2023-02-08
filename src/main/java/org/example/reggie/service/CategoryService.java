package org.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
