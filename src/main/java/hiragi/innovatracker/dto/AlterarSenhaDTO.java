package hiragi.innovatracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
        @NotBlank(message = "Senha Atual é obrigatória")
        @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres")
        String senhaAtual,

        @NotBlank(message = "A senha nova é obrigatória")
        @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres")
        String senhaNova
) {}
