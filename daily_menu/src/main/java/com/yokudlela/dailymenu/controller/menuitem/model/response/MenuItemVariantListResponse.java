package com.yokudlela.dailymenu.controller.menuitem.model.response;

import com.yokudlela.dailymenu.client.persistence.entity.enumeration.Variant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Schema(description = "List of menu item sections")
@Value
@SuperBuilder
@Jacksonized
public class MenuItemVariantListResponse {

    @Schema(description = "List of menu item variants")
    List<Variant> contents;

}
