package com.yokudlela.dailymenu.business.model.menu.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MenuHandler {

    Menu load(UUID id);

    List<Menu> loadAll(String page, String size);

    Menu findByDay(LocalDate day);

    List<Menu> findBetweenDayFromDayTo(LocalDate from, LocalDate to, String page, String size);

    Menu save(Menu object);

    void delete(UUID id);

}
