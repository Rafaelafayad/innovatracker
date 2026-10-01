package hiragi.innovatracker.repository;


import hiragi.innovatracker.model.TbPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface TbPessoaRepository extends JpaRepository<TbPessoa, Long> {


    Optional<TbPessoa> findByEmlPessoaIgnoreCase(String emlPessoa);


    boolean existsByEmlPessoaIgnoreCase(String emlPessoa);


    // Consulta aproximada por nome (derivada) -> LIKE %nome% ignorando caixa
    List<TbPessoa> findByNmePessoaContainingIgnoreCase(String nmePessoa);


    // Versão paginada
    Page<TbPessoa> findByNmePessoaContainingIgnoreCase(String nmePessoa, Pageable pageable);


    // Aproximada apenas entre pessoas ativas
    Page<TbPessoa> findByNmePessoaContainingIgnoreCaseAndFlgAtivoPessoaTrue(String nmePessoa, Pageable pageable);


}