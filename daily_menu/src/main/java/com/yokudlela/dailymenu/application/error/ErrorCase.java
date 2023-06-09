package com.yokudlela.dailymenu.application.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum ErrorCase {

    UNKNOWN_BUSINESS_ERROR("DMB-0000"),
    UNKNOWN_TECHNICAL_ERROR("DMT-0001"),
    VALIDATION_ERROR("DMB-0003"),

    DISH_NOT_FOUND("DMB-0010"),
    MENU_NOT_FOUND("DMB-0011"),
    MENU_ITEM_NOT_FOUND("DMB-0012"),
    MENU_ITEM_CATEGORY_NOT_FOUND("DMB-0013"),
    MENU_ITEM_SECTION_NOT_FOUND("DMB-0014"),
    MENU_ITEM_VARIANT_NOT_FOUND("DMB-0015"),
    DISH_ALREADY_EXISTS("DMB-0020"),
    MENU_ALREADY_EXISTS("DMB-0021"),

    RECIPE_NOT_FOUND("DMB-0080"),

    JSON_PROCESSING_FAILURE("DMT-0101"),
    AUTHENTICATION_FAILURE("DMT-0101"),
    AUTHORIZATION_FAILURE("DMT-0102"),
    RECIPE_SERVICE_UNAVAILABLE("DMT-0111");


    private String errorCode;

}
