package hiragi.innovatracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartamentoRequestDTO(
        @NotBlank(message = "A sigla do departamento é obrigatória")
        @Size(max = 10, message = "A sigla deve ter no máximo 10 caracteres")
        String sglDepartamento,

        @NotBlank(message = "O nome do departamento é obrigatório")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
        String nmeDepartamento
) {}