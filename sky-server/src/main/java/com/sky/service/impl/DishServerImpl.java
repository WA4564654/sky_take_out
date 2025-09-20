package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServerImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorsMapper dishFlavorsMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                //给每个口味赋值菜品Id
                flavor.setDishId(dishId);
            });
            //批量插入菜品口味
            dishFlavorsMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    public void deletBatch(List<Long> ids) {
        for (Long id : ids) {
            //判断菜品是否能被删除--菜品是否在售
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否能被删除--菜品是否与套餐关联
        List<Long> SetmealIds = setmealDishMapper.selectSetmealIdByDishId(ids);
        if (SetmealIds != null && SetmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        /*for (Long id : ids) {
            //删除菜品
            dishMapper.deleteDish(id);
            //删除菜品相关的口味
            dishFlavorsMapper.deleteFlavorsByDish(id);
        }*/

        //批量删除菜品
        dishMapper.deleteByIds(ids);
        //批量删除口味根据菜品ID
        dishFlavorsMapper.deleteFlavorsByDish(ids);
    }

    /**
     * 根据id查询菜品还有对应的口味
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavors(Long id) {
        //根据id查询菜品dish
        Dish dish = dishMapper.getById(id);
        //根据id查询口味集合
        List<DishFlavor> flavors =  dishFlavorsMapper.getByDishId(id);
        //赋值給dishVo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    public void update(DishDTO dishDTO) {
        //修改菜品的信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除口味数据
        dishFlavorsMapper.delete(dishDTO.getId());
        //新增口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorsMapper.insertBatch(flavors);
        }
    }
}
