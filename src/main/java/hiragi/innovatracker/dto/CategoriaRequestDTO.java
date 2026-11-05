package hiragi.innovatracker.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

        @NotBlank(message = "A sigla é obrigatória")
        @Size(max = 10, message = "A sigla deve ter no máximo 10 caracteres")
        String sglCategoria,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
        String nmeCategoria,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
        String dscCategoria
) {}
