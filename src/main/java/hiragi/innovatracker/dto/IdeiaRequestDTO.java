package hiragi.innovatracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record IdeiaRequestDTO(

        @NotNull(message = "A pessoa é obrigatória")
        Long codPessoa,

        @NotNull(message = "A categoria é obrigatória")
        Long codCategoria,

        @NotBlank(message = "O nome da ideia é obrigatório")
        @Size(max = 80, message = "O nome da ideia deve ter no máximo 80 caracteres")
        String nmeIdeia,

        @NotBlank(message = "O resumo da ideia é obrigatório")
        String txtResumoIdeia,

        @NotNull(message = "O valor do orçamento é obrigatório")
        @DecimalMin(value = "0.00", message = "O valor do orçamento deve ser maior ou igual a zero")
        @Digits(integer = 10, fraction = 2, message = "O orçamento deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal vlrOrcamentoIdeia,

        @Pattern(regexp = "^[SARPsarp]$", message = "O status deve ser 'S' (Submetida), 'A' (Aprovada), 'R' (Rejeitada) ou 'P' (Projeto)")
        @Size(max = 1, message = "O status deve ter apenas 1 caractere")
        String stsIdeia
) {}