package hiragi.innovatracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Types;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "tb_projeto", schema = "innova",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_projeto_cod_ideia", columnNames = {"cod_ideia"})
        }
)
public class TbProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_projeto", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtProjeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_ideia",
            referencedColumnName = "idt_ideia",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ideia_projeto")
    )
    private TbIdeia tbIdeia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_pessoa",
            referencedColumnName = "idt_pessoa",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pessoa_projeto")
    )
    private TbPessoa pessoaGerente;

    @Column(name = "nme_projeto", nullable = false, length = 80)
    private String nmeProjeto;

    @Column(name = "dsc_projeto", nullable = false, length = 200)
    private String dscProjeto;

    @Column(name = "vlr_orcamento_projeto", nullable = false, precision = 12, scale = 2)
    private BigDecimal vlrOrcamentoProjeto;

    @Column(name = "dta_inicio_projeto", nullable = false)
    private LocalDate dtaInicioProjeto;

    @Column(name = "dta_previsao_fim_projeto", nullable = false)
    private LocalDate dtaPrevisaoFimProjeto;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "sts_projeto", nullable = false, length = 1)
    private String stsProjeto; // 'P'lanejado, 'E'm Execução, 'C'oncluído, 'S'uspenso
}
