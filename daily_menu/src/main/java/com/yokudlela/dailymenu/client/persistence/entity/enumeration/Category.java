package com.yokudlela.dailymenu.client.persistence.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    soup(1),
    main(2),
    dessert(3);

    private int order;

}
