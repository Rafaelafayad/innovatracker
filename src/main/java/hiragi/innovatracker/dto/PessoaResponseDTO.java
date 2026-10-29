package hiragi.innovatracker.dto;


import hiragi.innovatracker.model.TbPessoa;


import java.time.LocalDate;
import java.time.LocalDateTime;


public record PessoaResponseDTO(
        Long idtPessoa,
        Long codDepartamento,
        String nmeDepartamento,
        String nmePessoa,
        String emlPessoa,
        LocalDate dtaNascimentoPessoa,
        Boolean flgAtivoPessoa,
        LocalDateTime dttCriacaoPessoa
) {
    public static PessoaResponseDTO from(TbPessoa p) {
        return new PessoaResponseDTO(
                p.getIdtPessoa(),
                p.getTdDepartamento() != null ? p.getTdDepartamento().getIdtDepartamento() : null,
                p.getTdDepartamento() != null ? p.getTdDepartamento().getNmeDepartamento() : null,
                p.getNmePessoa(),
                p.getEmlPessoa(),
                p.getDtaNascimentoPessoa(),
                p.getFlgAtivoPessoa(),
                p.getDttCriacaoPessoa()
        );
    }
}