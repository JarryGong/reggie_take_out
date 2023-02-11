package org.example.reggie.dto;

import lombok.Data;
import org.example.reggie.entity.Dish;
import org.example.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //口味
    private List<DishFlavor> flavors = new ArrayList<>();

    //菜品名
    private String categoryName;

    private Integer copies;
}
