package com.yokudlela.dailymenu.business.model.menu.service;

import com.yokudlela.dailymenu.business.BusinessConstant;
import com.yokudlela.dailymenu.business.model.menu.model.Menu;
import com.yokudlela.dailymenu.business.model.menu.model.MenuHandler;
import com.yokudlela.dailymenu.business.model.menu.service.converter.MenuEntityConverter;
import com.yokudlela.dailymenu.client.persistence.entity.MenuEntity;
import com.yokudlela.dailymenu.client.persistence.repository.MenuRepository;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;
import com.yokudlela.dailymenu.application.error.ErrorCase;
import com.yokudlela.dailymenu.business.component.QueryHelper;
import com.yokudlela.dailymenu.business.error.BusinessException;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Menu handling service.
 */
@MethodLogging
@RequiredArgsConstructor
@ApplicationScoped
public class MenuService implements MenuHandler {

    @Inject
    MenuEntityConverter converter;

    @Inject
    QueryHelper queryHelper;

    @Inject
    MenuRepository repo;


    @CacheResult(cacheName = BusinessConstant.MENU_CACHE)
    @Override
    public Menu load(@CacheKey UUID id) {
        return converter.toBusiness(
                repo.findById(id)
                        .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND)));
    }

    @CacheResult(cacheName = BusinessConstant.MENU_CACHE)
    @Override
    public List<Menu> loadAll(@CacheKey String page, @CacheKey String size) {
        return Optional.ofNullable(
                        repo.findAll(queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.MENU_CACHE)
    @Override
    public Menu findByDay(@CacheKey LocalDate day) {
        return converter.toBusiness(
                Optional.ofNullable(repo.findByDay(day))
                        .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND)));
    }

    @CacheResult(cacheName = BusinessConstant.MENU_CACHE)
    @Override
    public List<Menu> findBetweenDayFromDayTo(@CacheKey LocalDate from,@CacheKey LocalDate to,
            String page, String size) {
        return Optional.ofNullable(repo.findByDayBetween(from, to, queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheInvalidate(cacheName = BusinessConstant.MENU_CACHE)
    @CacheInvalidate(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public Menu save(Menu object) {
        MenuEntity entity = converter.toEntity(object);
        entity = repo.save(entity);

        return converter.toBusiness(entity);
    }

    @CacheInvalidate(cacheName = BusinessConstant.MENU_CACHE)
    @CacheInvalidate(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public void delete(UUID id) {
        try {
            repo.deleteById(id);
        } catch (IllegalArgumentException exc) {
            throw new BusinessException(ErrorCase.MENU_NOT_FOUND);
        }
    }
}
