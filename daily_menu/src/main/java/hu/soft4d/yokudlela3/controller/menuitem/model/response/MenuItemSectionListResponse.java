package hu.soft4d.yokudlela3.controller.menuitem.model.response;

import hu.soft4d.yokudlela3.client.persistence.entity.enumeration.Section;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Schema(description = "List of menu item sections")
@Value
@SuperBuilder
@Jacksonized
public class MenuItemSectionListResponse {

    @Schema(description = "List of menu item sections")
    List<Section> contents;

}
