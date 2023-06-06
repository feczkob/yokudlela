package hu.soft4d.yokudlela3.controller.menu.model.response;

import hu.soft4d.yokudlela3.business.model.menu.model.Menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Menu on given day")
@Value
@SuperBuilder
@Jacksonized
public class MenuResponse {

    @Schema(description = "Menu")
    Menu content;

}
