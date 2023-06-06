package hu.soft4d.yokudlela3.business.model.menuitem.service;

import hu.soft4d.yokudlela3.application.annotation.MethodLogging;
import hu.soft4d.yokudlela3.application.error.ErrorCase;
import hu.soft4d.yokudlela3.business.BusinessConstant;
import hu.soft4d.yokudlela3.business.component.QueryHelper;
import hu.soft4d.yokudlela3.business.error.BusinessException;
import hu.soft4d.yokudlela3.business.model.menuitem.model.MenuItem;
import hu.soft4d.yokudlela3.business.model.menuitem.model.MenuItemHandler;
import hu.soft4d.yokudlela3.business.model.menuitem.service.converter.MenuItemEntityConverter;
import hu.soft4d.yokudlela3.client.persistence.entity.MenuItemEntity;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Category;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Section;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Variant;
import hu.soft4d.yokudlela3.client.persistence.repository.MenuItemRepository;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Menu planning and associating service.
 */
@MethodLogging
@RequiredArgsConstructor
@ApplicationScoped
public class MenuItemService implements MenuItemHandler {

    @Inject
    MenuItemEntityConverter converter;

    @Inject
    QueryHelper queryHelper;

    @Inject
    MenuItemRepository repo;


    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public MenuItem load(@CacheKey UUID id) {
        final MenuItemEntity entity = repo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND));
        return converter.toBusiness(entity);
    }

    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public List<MenuItem> loadAll(@CacheKey String page, @CacheKey String size) {
        return Optional.ofNullable(
                        repo.findAll(queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.MENU_ITEM_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public List<MenuItem> findByDay(@CacheKey LocalDate day, @CacheKey String page, @CacheKey String size) {
        return Optional.ofNullable(
                        repo.findByMenu_Day(day, queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.MENU_ITEM_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public List<MenuItem> findDayFromDayTo(@CacheKey LocalDate from, @CacheKey LocalDate to,
            @CacheKey String page, @CacheKey String size) {
        return Optional.ofNullable(
                        repo.findByMenu_DayBetween(from, to, queryHelper.getPageable(page, size)))
                .orElseThrow(() -> new BusinessException(ErrorCase.MENU_ITEM_NOT_FOUND))
                .stream()
                .map(converter::toBusiness)
                .toList();
    }

    @CacheInvalidate(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public MenuItem save(MenuItem object) {
        MenuItemEntity entity = converter.toEntity(object);
        entity = repo.save(entity);

        return converter.toBusiness(entity);
    }

    @CacheInvalidate(cacheName = BusinessConstant.MENU_ITEM_CACHE)
    @Override
    public void delete(UUID id) {
        try {
            repo.deleteById(id);
        } catch (IllegalArgumentException exc) {
            throw new BusinessException(ErrorCase.MENU_NOT_FOUND);
        }
    }

    // --- ENUMS ---------------------------

    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_CATEGORY_CACHE)
    public List<Category> getAllCategories() {
        return Arrays.stream(Optional.ofNullable(Category.values())
                        .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND)))
                .sorted(Comparator.comparing(Category::getOrder))
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_SECTION_CACHE)
    public List<Section> getAllSections() {
        return Arrays.stream(Optional.ofNullable(Section.values())
                        .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND)))
                .sorted(Comparator.comparing(Section::getOrder))
                .toList();
    }

    @CacheResult(cacheName = BusinessConstant.MENU_ITEM_VARIANT_CACHE)
    public List<Variant> getAllVariants() {
        return Arrays.stream(Optional.ofNullable(Variant.values())
                        .orElseThrow(() -> new BusinessException(ErrorCase.MENU_NOT_FOUND)))
                .sorted(Comparator.comparing(Variant::getOrder))
                .toList();
    }

}
