package hiragi.innovatracker.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;


public record PessoaRequestDTO(


        @NotNull(message = "O departamento é obrigatório")
        Long codDepartamento,


        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
        String nmePessoa,


        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 80, message = "O e-mail deve ter no máximo 80 caracteres")
        String emlPessoa,


        /* Obrigatória na criação; opcional na atualização (null = mantém a atual). */
        @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres")
        String senha,


        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve ser anterior a hoje")
        LocalDate dtaNascimentoPessoa,


        Boolean flgAtivoPessoa
) {}