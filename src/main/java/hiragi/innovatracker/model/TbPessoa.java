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


import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "tb_pessoa",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pessoa_eml_pessoa", columnNames = {"eml_pessoa"})
        }
)
public class TbPessoa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_pessoa", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtPessoa;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cod_departamento",
            referencedColumnName = "idt_departamento",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_departamento_pessoa")
    )
    private TdDepartamento tdDepartamento;


    @Column(name = "nme_pessoa", nullable = false, length = 80)
    private String nmePessoa;


    @Column(name = "eml_pessoa", nullable = false, length = 80)
    private String emlPessoa;


    @Column(name = "pwd_pessoa", nullable = false, length = 128)
    private String pwdPessoa;


    @Column(name = "dta_nascimento_pessoa", nullable = false)
    private LocalDate dtaNascimentoPessoa;


    @Builder.Default
    @Column(name = "flg_ativo_pessoa", nullable = false)
    private Boolean flgAtivoPessoa = true;


    @Builder.Default
    @Column(name = "dtt_criacao_pessoa", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dttCriacaoPessoa = LocalDateTime.now();
}
