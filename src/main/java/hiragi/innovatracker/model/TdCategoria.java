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
        name = "td_categoria",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_categoria_sgl_categoria", columnNames = {"sgl_categoria"})
        }
)
public class TdCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_categoria", nullable = false)
    @EqualsAndHashCode.Include
    private Long idtCategoria;


    @Column(name = "sgl_categoria", nullable = false, length = 10)
    private String sglCategoria;


    @Column(name = "nme_categoria", nullable = false, length = 80)
    private String nmeCategoria;


    @Column(name = "dsc_categoria", nullable = false, length = 200)
    private String dscCategoria;
}
