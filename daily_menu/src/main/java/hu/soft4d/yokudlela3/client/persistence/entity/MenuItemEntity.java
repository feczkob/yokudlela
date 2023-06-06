package hu.soft4d.yokudlela3.client.persistence.entity;

import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Section;
import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Variant;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The type Menu item entity.
 */
@Table
@Entity(name = "item")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MenuItemEntity implements Serializable {

    private static final long serialVersionUID = 7286568035473668721L;

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

    @Enumerated(EnumType.STRING)
    private Section section;

    @Enumerated(EnumType.STRING)
    private Variant variant;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;

    @Type(type = "uuid-char")
    @Column(name = "menu_id", nullable = false, updatable = false, insertable = false)
    private UUID menuId;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "dish_id", nullable = false)
    private DishEntity dish;

    @Type(type = "uuid-char")
    @Column(name = "dish_id", nullable = false, updatable = false, insertable = false)
    private UUID dishId;

    private Integer price;

    @Column(name = "current_amount")
    private Integer currentAmount;

    @Column(name = "claimed_amount")
    private Integer claimedAmount;

    @Column(name = "paid_amount")
    private Integer paidAmount;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
