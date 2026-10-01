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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Types;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_ideia")
public class TbIdeia {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_ideia", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtIdeia;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_pessoa",
            referencedColumnName = "idt_pessoa",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pessoa_ideia")
    )
    private TbPessoa pessoa;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_categoria",
            referencedColumnName = "idt_categoria",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_categoria_ideia")
    )
    private TdCategoria tdCategoria;


    @Column(name = "nme_ideia", nullable = false, length = 80)
    private String nmeIdeia;


    @Column(name = "txt_resumo_ideia", nullable = false, columnDefinition = "TEXT")
    private String txtResumoIdeia;


    @Column(name = "vlr_orcamento_ideia", nullable = false, precision = 12, scale = 2)
    private BigDecimal vlrOrcamentoIdeia;


    @JdbcTypeCode(Types.CHAR)
    @Column(name = "sts_ideia", nullable = false, length = 1)
    private String stsIdeia; // 'S'ubmetida, 'A'provada, 'R'ejeitada, 'P'rojeto


    @Builder.Default
    @Column(name = "dtt_criacao_ideia", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dttCriacaoIdeia = LocalDateTime.now();
}
