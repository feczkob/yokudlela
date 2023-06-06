package hu.soft4d.yokudlela3.business.model.menuitem.model;

import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Category;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Section;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Variant;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MenuItemHandler {

    MenuItem load(UUID id);

    List<Category> getAllCategories();
    List<Section> getAllSections();
    List<Variant> getAllVariants();

    List<MenuItem> loadAll(String page, String size);

    List<MenuItem> findByDay(LocalDate day, String page, String size);

    List<MenuItem> findDayFromDayTo(LocalDate from, LocalDate to, String page, String size);

    MenuItem save(MenuItem object);

    void delete(UUID id);

}
