package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FooRequestDTO(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name
        // autres champs à ajouter ici
) {}
