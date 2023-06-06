package hu.soft4d.yokudlela3.controller.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

import javax.validation.constraints.NotNull;

@Value
@SuperBuilder
@Jacksonized
public class IdResponse {

    @Schema(description = "Unique identifier", example = "UUID", type = "uuid")
    @NonNull
    @NotNull(message = "error.request.correlationId.required")
    UUID id;


}
