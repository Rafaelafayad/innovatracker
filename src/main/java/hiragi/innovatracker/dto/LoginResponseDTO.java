package hiragi.innovatracker.dto;


public record LoginResponseDTO(
        String token,
        String tipo,
        Long expiraEm,
        PessoaResumoDTO pessoa
) {
    public static LoginResponseDTO of(String token, Long expiraEm, PessoaResumoDTO pessoa) {
        return new LoginResponseDTO(token, "Bearer", expiraEm, pessoa);
    }
}
