package hiragi.innovatracker.dto;

import hiragi.innovatracker.model.TbIdeia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IdeiaResponseDTO(
        Long idtIdeia,
        Long codPessoa,
        String nmePessoa,
        Long codCategoria,
        String nmeCategoria,
        String nmeIdeia,
        String txtResumoIdeia,
        BigDecimal vlrOrcamentoIdeia,
        String stsIdeia,
        LocalDateTime dttCriacaoIdeia
) {
    public static IdeiaResponseDTO from(TbIdeia i) {
        return new IdeiaResponseDTO(
                i.getIdtIdeia(),
                i.getPessoa() != null ? i.getPessoa().getIdtPessoa() : null,
                i.getPessoa() != null ? i.getPessoa().getNmePessoa() : null,
                i.getTdCategoria() != null ? i.getTdCategoria().getIdtCategoria() : null,
                i.getTdCategoria() != null ? i.getTdCategoria().getNmeCategoria() : null,
                i.getNmeIdeia(),
                i.getTxtResumoIdeia(),
                i.getVlrOrcamentoIdeia(),
                i.getStsIdeia(),
                i.getDttCriacaoIdeia()
        );
    }
}