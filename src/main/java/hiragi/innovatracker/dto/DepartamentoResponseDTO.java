package hiragi.innovatracker.dto;

import hiragi.innovatracker.model.TdDepartamento;

public record DepartamentoResponseDTO(
        Long idtDepartamento,
        String sglDepartamento,
        String nmeDepartamento
) {
    public static DepartamentoResponseDTO from(TdDepartamento departamento) {
        return new DepartamentoResponseDTO(
                departamento.getIdtDepartamento(),
                departamento.getSglDepartamento(),
                departamento.getNmeDepartamento()
        );
    }
}