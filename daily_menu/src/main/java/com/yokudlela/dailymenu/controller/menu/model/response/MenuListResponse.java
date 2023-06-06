package com.yokudlela.dailymenu.controller.menu.model.response;

import com.yokudlela.dailymenu.business.model.menu.model.Menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Schema(description = "Menus on given days")
@Value
@SuperBuilder
@Jacksonized
public class MenuListResponse {

    @Schema(description = "List of menus")
    List<Menu> contents;

}
