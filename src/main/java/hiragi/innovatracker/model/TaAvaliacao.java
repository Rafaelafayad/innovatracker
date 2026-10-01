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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ta_avaliacao", schema = "innova")
public class TaAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_avaliacao", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtAvaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_ideia",
            referencedColumnName = "idt_ideia",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ideia_avaliacao")
    )
    private TbIdeia tbIdeia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_pessoa",
            referencedColumnName = "idt_pessoa",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pessoa_avaliacao")
    )
    private TbPessoa pessoaAvaliador;

    @Column(name = "qtd_nota_avaliacao", nullable = false)
    private Integer qtdNotaAvaliacao;

    @Column(name = "txt_parecer_avaliacao", nullable = false, columnDefinition = "TEXT")
    private String txtParecerAvaliacao;

    @Builder.Default
    @Column(name = "dtt_criacao_avaliacao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dttCriacaoAvaliacao = LocalDateTime.now();
}
