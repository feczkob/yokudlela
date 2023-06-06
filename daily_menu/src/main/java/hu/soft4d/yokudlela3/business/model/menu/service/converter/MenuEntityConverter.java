package hu.soft4d.yokudlela3.business.model.menu.service.converter;

import hu.soft4d.yokudlela3.business.model.menu.model.Menu;
import hu.soft4d.yokudlela3.client.persistence.entity.MenuEntity;

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
