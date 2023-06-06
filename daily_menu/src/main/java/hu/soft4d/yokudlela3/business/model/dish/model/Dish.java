package hu.soft4d.yokudlela3.business.model.dish.model;

import hu.soft4d.yokudlela3.business.component.BeanMerger;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@ToString
@Getter
@Setter
@Dependent
public class Dish implements Serializable {

    private static final long serialVersionUID = 1559626200258175716L;

    @JsonIgnore
    @Inject
    DishHandler handler;

    @JsonIgnore
    @Inject
    BeanMerger beanMerger;

    private UUID id;
    private String name;
    private Integer recipe; // currently, just an ID, later can be generated type (Recipe) from Recipe API
    private Category category;
    private Integer price;
    private Boolean active;


    public Dish load() {
        beanMerger.write(this, handler.load(this.getId()));
        return this;
    }

    public List<Dish> loadAll(String page, String size) {
        return handler.loadAll(page, size);
    }

    public List<Dish> findAllActive(String page, String size) {
        return handler.findAllActive(page, size);
    }

    public List<Dish> findByNameAndActive(String name, Boolean active, String page, String size) {
        return handler.findByNameAndActive(name, active, page, size);
    }

    public Dish save() {
        beanMerger.write(this, handler.save(this));
        return this;
    }

    public void delete() {
        handler.delete(getId());
    }

}
