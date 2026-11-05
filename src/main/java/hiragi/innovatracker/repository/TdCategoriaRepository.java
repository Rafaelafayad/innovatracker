package hiragi.innovatracker.repository;

import hiragi.innovatracker.model.TdCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TdCategoriaRepository extends JpaRepository<TdCategoria, Long> {

    Optional<TdCategoria> findBySglCategoriaIgnoreCase(String sglCategoria);

    List<TdCategoria> findBySglCategoriaContainingOrNmeCategoriaContaining(String sgl, String nme);

    boolean existsBySglCategoriaIgnoreCase(String sglCategoria);

    // Busca exata pelo nome
    Optional<TdCategoria> findByNmeCategoriaIgnoreCase(String nmeCategoria);

    boolean existsByNmeCategoriaIgnoreCase(String nmeCategoria);

    // Busca parcial pelo nome (LIKE %valor%), ordenada
    List<TdCategoria> findByNmeCategoriaContainingIgnoreCaseOrderByNmeCategoriaAsc(String nmeCategoria);

}
