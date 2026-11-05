package hiragi.innovatracker.dto;

import hiragi.innovatracker.model.TbPessoa;

public record PessoaResumoDTO(
        Long idtPessoa,
        String nmePessoa,
        String emlPessoa,
        Long codDepartamento
) {
    public static PessoaResumoDTO from(TbPessoa pessoa) {
        return new PessoaResumoDTO(
                pessoa.getIdtPessoa(),
                pessoa.getNmePessoa(),
                pessoa.getEmlPessoa(),
                pessoa.getTdDepartamento() != null
                        ? pessoa.getTdDepartamento().getIdtDepartamento()
                        : null
        );
    }
}
