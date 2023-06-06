package com.yokudlela.dailymenu.controller.menu.model.response;

import com.yokudlela.dailymenu.business.model.menu.model.Menu;

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
