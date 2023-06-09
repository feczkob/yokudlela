package com.yokudlela.dailymenu.business.model.dish.service;

import com.yokudlela.dailymenu.business.BusinessConstant;
import com.yokudlela.dailymenu.business.model.dish.model.Dish;
import com.yokudlela.dailymenu.business.model.dish.model.DishHandler;
import com.yokudlela.dailymenu.business.model.dish.service.converter.DishEntityConverter;
import com.yokudlela.dailymenu.client.persistence.entity.DishEntity;
import com.yokudlela.dailymenu.client.persistence.repository.DishRepository;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;
import com.yokudlela.dailymenu.application.error.ErrorCase;
import com.yokudlela.dailymenu.business.component.QueryHelper;
import com.yokudlela.dailymenu.business.error.BusinessException;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Dish handling service.
 */
@MethodLogging
@RequiredArgsConstructor
@ApplicationScoped
public class DishService implements DishHandler {

    @Inject
    DishEntityConverter converter;

    @Inject
    QueryHelper queryHelper;

    @Inject
    DishRepository repo;


    @CacheResult(cacheName = BusinessConstant.DISH_CACHE)
    @Override
    public Dish load(@CacheKey UUID id) {
        return converter.toBusiness(
                repo.findById(id)
                        .orElseThrow(() -> new BusinessException(ErrorCase.DISH_NOT_FOUND)));
    }

    @CacheResult(cacheName = BusinessConstant.DISH_CACHE)
    @Override
    public List<Dish> loadAll(@CacheKey String page, @CacheKey String size) {
        return Optional.ofNullable(
                        repo.findAll(queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.DISH_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.DISH_CACHE)
    @Override
    public List<Dish> findAllActive(@CacheKey String page, @CacheKey String size) {
        return Optional.ofNullable(repo.findByActive(true, queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.DISH_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.DISH_CACHE)
    @Override
    public List<Dish> findByNameAndActive(@CacheKey String name, @CacheKey Boolean active,
            @CacheKey String page, @CacheKey String size) {
        final String nameParam = queryHelper.getLikeParamString(name);

        if (null == nameParam && null == active) {
            return loadAll(page, size);
        } else if (null == nameParam && null != active) {
            return Optional.ofNullable(repo.findByActive(active, queryHelper.getPageable(page, size)))
                    .orElseThrow(() -> new BusinessException(ErrorCase.DISH_NOT_FOUND))
                    .stream()
                    .map(converter::toBusiness)
                    .toList();
        } else if (null != nameParam && null == active) {
            return Optional.ofNullable(repo.findByNameLikeIgnoreCase(nameParam,
                            queryHelper.getPageable(page, size)))
                    .orElseThrow(() -> new BusinessException(ErrorCase.DISH_NOT_FOUND))
                    .stream()
                    .map(converter::toBusiness)
                    .toList();
        } else {
            return Optional.ofNullable(repo.findByNameLikeIgnoreCaseAndActive(nameParam, active,
                            queryHelper.getPageable(page, size)))
                    .orElseThrow(() -> new BusinessException(ErrorCase.DISH_NOT_FOUND))
                    .stream()
                    .map(converter::toBusiness)
                    .toList();
        }
    }

    @CacheInvalidate(cacheName = BusinessConstant.DISH_CACHE)
    @CacheInvalidate(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public Dish save(Dish object) {
        DishEntity entity = converter.toEntity(object);
        entity = repo.save(entity);

        return converter.toBusiness(entity);
    }

    @CacheInvalidate(cacheName = BusinessConstant.DISH_CACHE)
    @CacheInvalidate(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public void delete(UUID id) {
        try {
            repo.deleteById(id);
        } catch (IllegalArgumentException exc) {
            throw new BusinessException(ErrorCase.DISH_NOT_FOUND);
        }
    }
}
