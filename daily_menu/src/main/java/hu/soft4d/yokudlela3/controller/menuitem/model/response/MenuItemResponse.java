package hu.soft4d.yokudlela3.controller.menuitem.model.response;

import hu.soft4d.yokudlela3.business.model.menuitem.model.MenuItem;

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
