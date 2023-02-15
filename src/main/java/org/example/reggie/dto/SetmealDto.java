package org.example.reggie.dto;

import lombok.Data;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
