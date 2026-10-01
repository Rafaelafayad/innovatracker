package hiragi.innovatracker.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_apontamento")
public class TbApontamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_apontamento", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtApontamento;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_projeto_pessoa",
            referencedColumnName = "idt_projeto_pessoa",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_projeto_pessoa_apontamento")
    )
    private TaProjetoPessoa taProjetoPessoa;

    @Column(name = "dta_apontamento", nullable = false)
    private LocalDate dtaApontamento;


    @Column(name = "qtd_horas_apontamento", nullable = false)
    private Integer qtdHorasApontamento;

    @Column(name = "txt_descricao_apontamento", nullable = false, columnDefinition = "TEXT")
    private String txtDescricaoApontamento;

    @Builder.Default
    @Column(name = "dtt_registro_apontamento", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dttRegistroApontamento = LocalDateTime.now();
}
