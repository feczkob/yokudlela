package com.yokudlela.dailymenu.client.persistence.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Variant {

    A(1),
    B(2),
    C(3);

    private int order;

}
