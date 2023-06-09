package com.yokudlela.dailymenu.business.model.dish.service.converter;

import com.yokudlela.dailymenu.business.model.dish.model.Dish;
import com.yokudlela.dailymenu.client.persistence.entity.DishEntity;

import org.modelmapper.ModelMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class DishEntityConverter {

    @Inject
    ModelMapper modelMapper;


    public Dish toBusiness(DishEntity entity) {
        return null == entity
                ? null
                : modelMapper.map(entity, Dish.class);
    }

    public DishEntity toEntity(Dish object) {
        return  null == object
                ? null
                : modelMapper.map(object, DishEntity.class);
    }
}
