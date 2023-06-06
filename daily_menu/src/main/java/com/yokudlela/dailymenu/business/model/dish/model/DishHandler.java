package com.yokudlela.dailymenu.business.model.dish.model;

import java.util.List;
import java.util.UUID;

public interface DishHandler {

    Dish load(UUID id);

    List<Dish> loadAll(String page, String size);

    List<Dish> findAllActive(String page, String size);

    List<Dish> findByNameAndActive(String name, Boolean active, String page, String size);

    Dish save(Dish object);

    void delete(UUID id);

}
