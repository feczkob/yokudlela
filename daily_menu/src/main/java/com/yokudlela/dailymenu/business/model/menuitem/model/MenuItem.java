package com.yokudlela.dailymenu.business.model.menuitem.model;

import com.yokudlela.dailymenu.business.model.dish.model.Dish;
import com.yokudlela.dailymenu.business.model.dish.model.DishHandler;
import com.yokudlela.dailymenu.business.model.menu.model.Menu;
import com.yokudlela.dailymenu.business.model.menu.model.MenuHandler;
import com.yokudlela.dailymenu.client.persistence.entity.enumeration.Category;
import com.yokudlela.dailymenu.client.persistence.entity.enumeration.Section;
import com.yokudlela.dailymenu.client.persistence.entity.enumeration.Variant;
import com.yokudlela.dailymenu.business.component.BeanMerger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@ToString
@Getter
@Setter
@Dependent
public class MenuItem implements Serializable {

    private static final long serialVersionUID = -8140984075543239854L;

    @JsonIgnore
    @Inject
    MenuItemHandler handler;

    @JsonIgnore
    @Inject
    DishHandler dishHandler;

    @JsonIgnore
    @Inject
    MenuHandler menuHandler;

    @JsonIgnore
    @Inject
    BeanMerger beanMerger;

    private UUID id;
    private Section section;
    private Variant variant;
    private UUID menuId;
    private Menu menu;
    private UUID dishId;
    private Dish dish;
    private Integer price;
    private Integer currentAmount;
    private Integer claimedAmount;
    private Integer paidAmount;


    public MenuItem load() {
        beanMerger.write(this, handler.load(this.getId()));
        return this;
    }

    public List<MenuItem> loadAll(String page, String size) {
        return handler.loadAll(page, size);
    }

    public List<MenuItem> findMenuItemsByDay(LocalDate day, String page, String size) {
        return handler.findByDay(day, page, size);
    }

    public List<MenuItem> findMenuItemsBetweenDayFromDayTo(LocalDate from, LocalDate to,
            String page, String size) {
        return handler.findDayFromDayTo(from, to, page, size);
    }

    public List<Category> provideAllCategories() {
        return handler.getAllCategories();
    }

    public List<Section> provideAllSections() {
        return handler.getAllSections();
    }

    public List<Variant> provideAllVariants() {
        return handler.getAllVariants();
    }

    public MenuItem save() {
        setDish(dishHandler.load(getDishId()));
        setMenu(menuHandler.load(getMenuId()));
        beanMerger.write(this, handler.save(this));
        return this;
    }

    public void delete() {
        handler.delete(getId());
    }

    public MenuItem claim(Integer amount) {
        load();
        setClaimedAmount(getClaimedAmount() + amount);
        // TODO: call Kitchen microservice
        return save();
    }

    public MenuItem done(Integer amount) {
        load();
        setClaimedAmount(getClaimedAmount() - amount);
        setCurrentAmount(getCurrentAmount() + amount);
        return save();
    }

    public MenuItem pay(Integer amount) {
        load();
        setCurrentAmount(getCurrentAmount() - amount);
        setPaidAmount(getPaidAmount() + amount);
        return save();
    }

}
