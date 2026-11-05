package hiragi.innovatracker.repository;

import hiragi.innovatracker.model.TdDepartamento;
import hiragi.innovatracker.model.TdDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TdDepartamentoRepository extends JpaRepository<TdDepartamento, Long> {

    Optional<TdDepartamento> findBySglDepartamentoIgnoreCase(String sglDepartamento);

    List<TdDepartamento> findBySglDepartamentoContainingOrNmeDepartamentoContaining(String sgl, String nme);

    boolean existsBySglDepartamentoIgnoreCase(String sglDepartamento);

    // Busca exata pelo nome
    Optional<TdDepartamento> findByNmeDepartamentoIgnoreCase(String nmeDepartamento);

    boolean existsByNmeDepartamentoIgnoreCase(String nmeDepartamento);

    // Busca parcial pelo nome (LIKE %valor%), ordenada
    List<TdDepartamento> findByNmeDepartamentoContainingIgnoreCaseOrderByNmeDepartamentoAsc(String nmeDepartamento);

}
