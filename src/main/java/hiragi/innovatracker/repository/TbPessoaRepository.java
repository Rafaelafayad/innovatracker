package hiragi.innovatracker.repository;


import hiragi.innovatracker.model.TbPessoa;
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
public interface TbPessoaRepository extends JpaRepository<TbPessoa, Long> {


    // ---------------- E-mail ----------------


    Optional<TbPessoa> findByEmlPessoaIgnoreCase(String emlPessoa);


    boolean existsByEmlPessoaIgnoreCase(String emlPessoa);


    /**
     * Login e /me: carrega o departamento junto para evitar LazyInitializationException.
     */
    @Query("SELECT p FROM TbPessoa p JOIN FETCH p.tdDepartamento WHERE LOWER(p.emlPessoa) = LOWER(:email)")
    Optional<TbPessoa> findComDepartamentoPorEmail(@Param("email") String email);


    // ---------------- Id ----------------


    /**
     * Usado pelo Service em todas as operações que montam o DTO.
     */
    @Query("SELECT p FROM TbPessoa p JOIN FETCH p.tdDepartamento WHERE p.idtPessoa = :id")
    Optional<TbPessoa> findComDepartamento(@Param("id") Long id);


    // ---------------- Nome ----------------




    /**
     * Ordenada — usada em pesquisarPorNome().
     */
    List<TbPessoa> findByNmePessoaContainingIgnoreCaseOrderByNmePessoaAsc(String nmePessoa);


    // ---------------- Departamento ----------------
    List<TbPessoa> findByTdDepartamento_IdtDepartamentoOrderByNmePessoaAsc(Long idtDepartamento);


    // ---------------- Listagens com fetch ----------------
    @Override
    @EntityGraph(attributePaths = {"tdDepartamento"})
    Page<TbPessoa> findAll(Pageable pageable);


}