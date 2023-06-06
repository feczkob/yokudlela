package hu.soft4d.yokudlela3.business.model.menuitem.service.converter;

import hu.soft4d.yokudlela3.business.model.menuitem.model.MenuItem;
import hu.soft4d.yokudlela3.client.persistence.entity.MenuItemEntity;

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
