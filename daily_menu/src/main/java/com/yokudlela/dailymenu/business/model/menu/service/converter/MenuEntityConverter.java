package com.yokudlela.dailymenu.business.model.menu.service.converter;

import com.yokudlela.dailymenu.client.persistence.entity.MenuEntity;
import com.yokudlela.dailymenu.business.model.menu.model.Menu;

import org.modelmapper.ModelMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MenuEntityConverter {

    @Inject
    ModelMapper modelMapper;


    public Menu toBusiness(MenuEntity entity) {
        return null == entity
                ? null
                : modelMapper.map(entity, Menu.class);
    }

    public MenuEntity toEntity(Menu object) {
        return  null == object
            ? null
            : modelMapper.map(object, MenuEntity.class);
    }
}
