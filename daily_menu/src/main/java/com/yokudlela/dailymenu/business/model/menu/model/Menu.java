package com.yokudlela.dailymenu.business.model.menu.model;


import com.yokudlela.dailymenu.business.component.BeanMerger;
import com.yokudlela.dailymenu.business.model.menuitem.model.MenuItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@ToString
@Getter
@Setter
@Dependent
public class Menu implements Serializable {

    private static final long serialVersionUID = -1697168463707996736L;

    @JsonIgnore
    @Inject
    MenuHandler handler;

    @JsonIgnore
    @Inject
    BeanMerger beanMerger;

    private UUID id;
    private LocalDate day;
    @JsonIgnore
    private Set<MenuItem> menuItems = new LinkedHashSet<>();


    public Menu load() {
        beanMerger.write(this, handler.load(this.getId()));
        return this;
    }

    public List<Menu> loadAll(String page, String size) {
        return handler.loadAll(page, size);
    }

    public Menu findMenuByDay(LocalDate day) {
        return handler.findByDay(day);
    }

    public List<Menu> findMenusBetweenDayFromDayTo(LocalDate from, LocalDate to, String page, String size) {
        return handler.findBetweenDayFromDayTo(from, to, page, size);
    }

    public Menu save() {
        beanMerger.write(this, handler.save(this));
        return this;
    }

    public void delete() {
        handler.delete(getId());
    }

}
