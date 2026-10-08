package hiragi.innovatracker.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginRequestDTO(


        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 80)
        String email,


        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}
