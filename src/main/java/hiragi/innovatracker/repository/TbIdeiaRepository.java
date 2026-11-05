package hiragi.innovatracker.repository;

import hiragi.innovatracker.model.TbIdeia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbIdeiaRepository extends JpaRepository<TbIdeia, Long> {

    // Busca aproximada por nome (LIKE %...%, ignorando maiúsculas/minúsculas)
    Page<TbIdeia> findByNmeIdeiaContainingIgnoreCase(String nmeIdeia, Pageable pageable);


    List<TbIdeia> findByNmeIdeiaContainingIgnoreCase(String nmeIdeia);


    // Busca por status ('S', 'A', 'R', 'P')
    Page<TbIdeia> findByStsIdeia(String stsIdeia, Pageable pageable);


    List<TbIdeia> findByStsIdeia(String stsIdeia);


    // Combinação: nome aproximado + status
    Page<TbIdeia> findByNmeIdeiaContainingIgnoreCaseAndStsIdeia(
            String nmeIdeia, String stsIdeia, Pageable pageable);


    // Filtro flexível: parâmetros nulos são ignorados
    @Query("""
           SELECT i FROM TbIdeia i
           WHERE (:nme IS NULL OR LOWER(i.nmeIdeia) LIKE LOWER(CONCAT('%', :nme, '%')))
             AND (:sts IS NULL OR i.stsIdeia = :sts)
           """)
    Page<TbIdeia> buscarPorNomeEStatus(@Param("nme") String nme,
                                       @Param("sts") String sts,
                                       Pageable pageable);
}
