package dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FooResponseDTO(

        @Schema(description = "Identifiant unique de la ressource Foo", example = "1")
        Long id,

        @Schema(description = "Nom de la ressource Foo", example = "Mon Super Foo")
        String name

) {}
