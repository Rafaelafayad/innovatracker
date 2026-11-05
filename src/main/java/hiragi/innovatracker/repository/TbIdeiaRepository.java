package hiragi.innovatracker.repository;

import hiragi.innovatracker.model.TbIdeia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbIdeiaRepository extends JpaRepository<TbIdeia, Long> {

    // ---------------- Id ----------------

    /**
     * Usado pelo Service em todas as operações que montam o DTO completo com os relacionamentos.
     */
    @Query("SELECT i FROM TbIdeia i JOIN FETCH i.pessoa JOIN FETCH i.tdCategoria WHERE i.idtIdeia = :id")
    Optional<TbIdeia> findComRelacionamentos(@Param("id") Long id);

    // ---------------- Nome ----------------

    /**
     * Ordenada — usada em pesquisarPorNome().
     */
    List<TbIdeia> findByNmeIdeiaContainingIgnoreCaseOrderByNmeIdeiaAsc(String nmeIdeia);

    // ---------------- Pessoa ----------------

    List<TbIdeia> findByPessoa_IdtPessoaOrderByNmeIdeiaAsc(Long idtPessoa);

    // ---------------- Categoria ----------------

    List<TbIdeia> findByTdCategoria_IdtCategoriaOrderByNmeIdeiaAsc(Long idtCategoria);

    // ---------------- Status ----------------

    List<TbIdeia> findByStsIdeiaOrderByNmeIdeiaAsc(String stsIdeia);

    // ---------------- Listagens com fetch ----------------

    @Override
    @EntityGraph(attributePaths = {"pessoa", "tdCategoria"})
    Page<TbIdeia> findAll(Pageable pageable);
}