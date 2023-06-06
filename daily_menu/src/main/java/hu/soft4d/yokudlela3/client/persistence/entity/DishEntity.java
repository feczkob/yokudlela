package hu.soft4d.yokudlela3.client.persistence.entity;

import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Entity(name = "dish")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DishEntity implements Serializable {

    private static final long serialVersionUID = 2340060096908154721L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @Type(type = "uuid-char")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String name;

    private Integer recipe; // currently, just an ID, later can be generated type (Recipe) from Recipe API

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer price;

    private Boolean active = Boolean.FALSE;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
