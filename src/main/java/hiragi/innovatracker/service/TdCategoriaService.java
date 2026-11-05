package hiragi.innovatracker.service;


import hiragi.innovatracker.dto.CategoriaRequestDTO;
import hiragi.innovatracker.dto.CategoriaResponseDTO;
import hiragi.innovatracker.exception.BusinessException;
import hiragi.innovatracker.exception.ResourceNotFoundException;
import hiragi.innovatracker.model.TdCategoria;
import hiragi.innovatracker.repository.TdCategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TdCategoriaService {


    private final TdCategoriaRepository categoriaRepository;


    // ---------------------- LEITURA ----------------------


    @Transactional(readOnly = true)
    public Page<CategoriaResponseDTO> listar(Pageable pageable) {
        return categoriaRepository.findAll(pageable)
                .map(CategoriaResponseDTO::from);
    }


    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository
                .findByNmeCategoriaContainingIgnoreCaseOrderByNmeCategoriaAsc("")
                .stream()
                .map(CategoriaResponseDTO::from)
                .toList();
    }


    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        return CategoriaResponseDTO.from(obterEntidade(id));
    }


    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorSigla(String sigla) {
        String valor = normalizar(sigla);
        return categoriaRepository.findBySglCategoriaIgnoreCase(valor)
                .map(CategoriaResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada para a sigla: " + valor));
    }


    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> pesquisarPorNome(String nome) {
        return categoriaRepository
                .findByNmeCategoriaContainingIgnoreCaseOrderByNmeCategoriaAsc(normalizar(nome))
                .stream()
                .map(CategoriaResponseDTO::from)
                .toList();
    }


    // ---------------------- ESCRITA ----------------------


    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO request) {
        String sigla = normalizar(request.sglCategoria());
        String nome = normalizar(request.nmeCategoria());


        if (categoriaRepository.existsBySglCategoriaIgnoreCase(sigla)) {
            throw new BusinessException("Já existe uma categoria com a sigla: " + sigla);
        }
        if (categoriaRepository.existsByNmeCategoriaIgnoreCase(nome)) {
            throw new BusinessException("Já existe uma categoria com o nome: " + nome);
        }


        TdCategoria categoria = TdCategoria.builder()
                .sglCategoria(sigla)
                .nmeCategoria(nome)
                .dscCategoria(normalizar(request.dscCategoria()))
                .build();


        TdCategoria salva = categoriaRepository.save(categoria);
        log.info("Categoria criada: idtCategoria={}, sigla={}", salva.getIdtCategoria(), sigla);


        return CategoriaResponseDTO.from(salva);
    }


    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO request) {
        TdCategoria categoria = obterEntidade(id);


        String sigla = normalizar(request.sglCategoria());
        String nome = normalizar(request.nmeCategoria());


        validarSiglaDuplicada(sigla, id);
        validarNomeDuplicado(nome, id);


        categoria.setSglCategoria(sigla);
        categoria.setNmeCategoria(nome);
        categoria.setDscCategoria(normalizar(request.dscCategoria()));


        log.info("Categoria atualizada: idtCategoria={}", id);


        // dirty checking já persistiria; save() explícito para clareza
        return CategoriaResponseDTO.from(categoriaRepository.save(categoria));
    }


    @Transactional
    public void excluir(Long id) {
        TdCategoria categoria = obterEntidade(id);


        try {
            categoriaRepository.delete(categoria);
            categoriaRepository.flush(); // força o erro aqui, e não no commit
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha ao excluir categoria idtCategoria={}: {}", id, e.getMostSpecificCause().getMessage());
            throw new BusinessException(
                    "Não é possível excluir a categoria: existem registros vinculados a ela.");
        }


        log.info("Categoria excluída: idtCategoria={}", id);
    }


    // ---------------------- APOIO ----------------------


    private TdCategoria obterEntidade(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada para o id: " + id));
    }


    /** Permite manter a própria sigla, mas bloqueia se pertencer a outra categoria. */
    private void validarSiglaDuplicada(String sigla, Long idAtual) {
        categoriaRepository.findBySglCategoriaIgnoreCase(sigla)
                .filter(existente -> !existente.getIdtCategoria().equals(idAtual))
                .ifPresent(existente -> {
                    throw new BusinessException("Já existe uma categoria com a sigla: " + sigla);
                });
    }


    private void validarNomeDuplicado(String nome, Long idAtual) {
        categoriaRepository.findByNmeCategoriaIgnoreCase(nome)
                .filter(existente -> !existente.getIdtCategoria().equals(idAtual))
                .ifPresent(existente -> {
                    throw new BusinessException("Já existe uma categoria com o nome: " + nome);
                });
    }


    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
