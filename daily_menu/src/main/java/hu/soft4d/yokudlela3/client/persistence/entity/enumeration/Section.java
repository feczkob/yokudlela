package hu.soft4d.yokudlela3.client.persistence.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Section {

    normal(1),
    vegan(2),
    child(3);

    private int order;

}
