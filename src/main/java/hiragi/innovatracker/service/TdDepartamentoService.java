package hiragi.innovatracker.service;

import hiragi.innovatracker.dto.DepartamentoRequestDTO;
import hiragi.innovatracker.dto.DepartamentoResponseDTO;
import hiragi.innovatracker.exception.BusinessException;
import hiragi.innovatracker.exception.ResourceNotFoundException;
import hiragi.innovatracker.model.TdDepartamento;
import hiragi.innovatracker.repository.TdDepartamentoRepository;
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
public class TdDepartamentoService {

    private final TdDepartamentoRepository departamentoRepository;

    // ---------------------- LEITURA ----------------------
    @Transactional(readOnly = true)
    public Page<DepartamentoResponseDTO> listar(Pageable pageable) {
        return departamentoRepository.findAll(pageable)
                .map(DepartamentoResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResponseDTO> listarTodos() {
        return departamentoRepository
                .findByNmeDepartamentoContainingIgnoreCaseOrderByNmeDepartamentoAsc("")
                .stream()
                .map(DepartamentoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartamentoResponseDTO buscarPorId(Long id) {
        return DepartamentoResponseDTO.from(obterEntidade(id));
    }

    @Transactional(readOnly = true)
    public DepartamentoResponseDTO buscarPorSigla(String sigla) {
        String valor = normalizar(sigla);
        return departamentoRepository.findBySglDepartamentoIgnoreCase(valor)
                .map(DepartamentoResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento não encontrado para a sigla: " + valor));
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResponseDTO> pesquisarPorNome(String nome) {
        return departamentoRepository
                .findByNmeDepartamentoContainingIgnoreCaseOrderByNmeDepartamentoAsc(normalizar(nome))
                .stream()
                .map(DepartamentoResponseDTO::from)
                .toList();
    }

    // ---------------------- ESCRITA ----------------------
    @Transactional
    public DepartamentoResponseDTO criar(DepartamentoRequestDTO request) {
        String sigla = normalizar(request.sglDepartamento());
        String nome = normalizar(request.nmeDepartamento());

        if (departamentoRepository.existsBySglDepartamentoIgnoreCase(sigla)) {
            throw new BusinessException("Já existe um departamento com a sigla: " + sigla);
        }

        TdDepartamento departamento = TdDepartamento.builder()
                .sglDepartamento(sigla)
                .nmeDepartamento(nome)
                .build();

        TdDepartamento salvo = departamentoRepository.save(departamento);
        log.info("Departamento criado: idtDepartamento={}, sigla={}", salvo.getIdtDepartamento(), sigla);
        return DepartamentoResponseDTO.from(salvo);
    }

    @Transactional
    public DepartamentoResponseDTO atualizar(Long id, DepartamentoRequestDTO request) {
        TdDepartamento departamento = obterEntidade(id);

        String sigla = normalizar(request.sglDepartamento());
        String nome = normalizar(request.nmeDepartamento());

        validarSiglaDuplicada(sigla, id);

        departamento.setSglDepartamento(sigla);
        departamento.setNmeDepartamento(nome);

        log.info("Departamento atualizado: idtDepartamento={}", id);
        return DepartamentoResponseDTO.from(departamentoRepository.save(departamento));
    }

    @Transactional
    public void excluir(Long id) {
        TdDepartamento departamento = obterEntidade(id);
        try {
            departamentoRepository.delete(departamento);
            departamentoRepository.flush(); // Força a verificação de FK antes do commit
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha ao excluir departamento idtDepartamento={}: {}", id, e.getMostSpecificCause().getMessage());
            throw new BusinessException(
                    "Não é possível excluir o departamento: existem registros vinculados a ele.");
        }
        log.info("Departamento excluído: idtDepartamento={}", id);
    }

    // ---------------------- APOIO ----------------------
    private TdDepartamento obterEntidade(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento não encontrado para o id: " + id));
    }

    /** Permite manter a própria sigla, mas bloqueia se pertencer a outro departamento. */
    private void validarSiglaDuplicada(String sigla, Long idAtual) {
        departamentoRepository.findBySglDepartamentoIgnoreCase(sigla)
                .filter(existente -> !existente.getIdtDepartamento().equals(idAtual))
                .ifPresent(existente -> {
                    throw new BusinessException("Já existe um departamento com a sigla: " + sigla);
                });
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}