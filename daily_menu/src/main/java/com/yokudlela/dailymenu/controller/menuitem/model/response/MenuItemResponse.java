package com.yokudlela.dailymenu.controller.menuitem.model.response;

import com.yokudlela.dailymenu.business.model.menuitem.model.MenuItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Menu item as a part of a menu on given day")
@Value
@SuperBuilder
@Jacksonized
public class MenuItemResponse {

    @Schema(description = "Menu item")
    MenuItem content;

}
