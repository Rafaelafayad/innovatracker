package hiragi.innovatracker.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "td_departamento",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_departamento_sgl_departamento", columnNames = {"sgl_departamento"})
        }
)
public class TdDepartamento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_departamento", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtDepartamento;


    @Column(name = "sgl_departamento", nullable = false, length = 10)
    private String sglDepartamento;


    @Column(name = "nme_departamento", nullable = false, length = 80)
    private String nmeDepartamento;
}
