package com.yokudlela.dailymenu.business.model.menuitem.service.converter;

import com.yokudlela.dailymenu.client.persistence.entity.MenuItemEntity;
import com.yokudlela.dailymenu.business.model.menuitem.model.MenuItem;

import org.modelmapper.ModelMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MenuItemEntityConverter {

    @Inject
    ModelMapper modelMapper;


    public MenuItem toBusiness(MenuItemEntity entity) {
        return null == entity
                ? null
                : modelMapper.map(entity, MenuItem.class);
    }

    public MenuItemEntity toEntity(MenuItem object) {
        return  null == object
                ? null
                : modelMapper.map(object, MenuItemEntity.class);
    }
}
