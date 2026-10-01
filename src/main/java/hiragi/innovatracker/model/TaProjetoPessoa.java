package hiragi.innovatracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ta_projeto_pessoa", schema = "innova")
public class TaProjetoPessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_projeto_pessoa", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtProjetoPessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_projeto",
            referencedColumnName = "idt_projeto",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_projeto_projeto_pessoa")
    )
    private TbProjeto tbProjeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_pessoa",
            referencedColumnName = "idt_pessoa",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pessoa_projeto_pessoa")
    )
    private TbPessoa tbPessoa;

    @Column(name = "dsc_papel_projeto_pessoa", nullable = false, length = 200)
    private String dscPapelProjetoPessoa;

    @Builder.Default
    @Column(name = "dta_alocacao_projeto_pessoa", nullable = false, insertable = false, updatable = false)
    private LocalDate dttCriacaoAvaliacao = LocalDate.now();

    @Builder.Default
    @Column(name = "flg_ativo_projeto_pessoa", nullable = false)
    private Boolean flgAtivoProjetoPessoa = true;
}
