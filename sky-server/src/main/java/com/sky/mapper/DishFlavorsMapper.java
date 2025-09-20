package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorsMapper {

    /**
     * 批量插入菜品口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品Id来批量删除口味
     * @param dishIds
     */
    void deleteFlavorsByDish(List<Long> dishIds);

    /**
     * 根据dishId查询口味集合
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 根据菜品Id删除单个口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void delete(Long id);
}
