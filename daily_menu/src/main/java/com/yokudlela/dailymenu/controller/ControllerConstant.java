package com.yokudlela.dailymenu.controller;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ControllerConstant {

    public static final String API_V1 = "/api/v1";

    public static final String CORRELATION_ID_HEADER_ATTRIBUTE = "Correlation-ID";
    public static final String CORRELATION_ID_LOG_PREFIX = "CORRELATION_ID";
    public static final String MENU_PATH = "/menu";
    public static final String MENU_ITEM_PATH = "/item";
    public static final String MENU_ITEM_ENUM_PATH = "/item/enum";
    public static final String DISH_PATH = "/dish";
    public static final String SLASH = "/";
    public static final String ID = "id";
    public static final String ID_PARAM = "{id}";

    public static final String API_DOC_RESPONSE_CODE_SUCCESS = "200";
    public static final String API_DOC_RESPONSE_DESC_SUCCESS = "Successful operation";

    public static final String API_DOC_RESPONSE_CODE_SUCCESSFULLY_CREATED = "201";
    public static final String API_DOC_RESPONSE_DESC_SUCCESSFULLY_CREATED = "Successfully created";

    public static final String API_DOC_RESPONSE_CODE_SUCCESSFULLY_DELETED = "204";
    public static final String API_DOC_RESPONSE_DESC_SUCCESSFULLY_DELETED = "Successfully deleted";

    public static final String API_DOC_RESPONSE_CODE_BAD_REQUEST = "400";
    public static final String API_DOC_RESPONSE_DESC_BAD_REQUEST = "Bad request";

    public static final String API_DOC_RESPONSE_CODE_FORBIDDEN = "403";
    public static final String API_DOC_RESPONSE_DESC_FORBIDDEN = "Forbidden";

    public static final String API_DOC_RESPONSE_CODE_NOT_FOUND = "404";
    public static final String API_DOC_RESPONSE_DESC_NOT_FOUND = "Resource not found";

    public static final String API_DOC_RESPONSE_CODE_INTERNAL_ERROR = "500";
    public static final String API_DOC_RESPONSE_DESC_INTERNAL_ERROR = "Internal server error";

    public static final String PAGE_PARAM_DESCRIPTION = "Zero based page number of result list";
    public static final String PAGE_PARAM_DEFAULT_VALUE = "0";
    public static final String SIZE_PARAM_DESCRIPTION = "Size of one page of result list";
    public static final String SIZE_PARAM_DEFAULT_VALUE = "20";

}
