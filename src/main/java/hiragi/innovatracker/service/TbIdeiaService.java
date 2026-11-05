package hiragi.innovatracker.service;

import hiragi.innovatracker.dto.IdeiaRequestDTO;
import hiragi.innovatracker.dto.IdeiaResponseDTO;
import hiragi.innovatracker.exception.BusinessException;
import hiragi.innovatracker.exception.ResourceNotFoundException;
import hiragi.innovatracker.model.TbIdeia;
import hiragi.innovatracker.model.TbPessoa;
import hiragi.innovatracker.model.TdCategoria;
import hiragi.innovatracker.repository.TbIdeiaRepository;
import hiragi.innovatracker.repository.TbPessoaRepository;
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
public class TbIdeiaService {

    private final TbIdeiaRepository ideiaRepository;
    private final TbPessoaRepository pessoaRepository;
    private final TdCategoriaRepository categoriaRepository;

    // ---------------------- LEITURA ----------------------

    @Transactional(readOnly = true)
    public Page<IdeiaResponseDTO> listar(Pageable pageable) {
        return ideiaRepository.findAll(pageable)
                .map(IdeiaResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public IdeiaResponseDTO buscarPorId(Long id) {
        return IdeiaResponseDTO.from(obterEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<IdeiaResponseDTO> pesquisarPorNome(String nome) {
        return ideiaRepository
                .findByNmeIdeiaContainingIgnoreCaseOrderByNmeIdeiaAsc(normalizar(nome))
                .stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IdeiaResponseDTO> listarPorPessoa(Long codPessoa) {
        return ideiaRepository
                .findByPessoa_IdtPessoaOrderByNmeIdeiaAsc(codPessoa)
                .stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IdeiaResponseDTO> listarPorCategoria(Long codCategoria) {
        return ideiaRepository
                .findByTdCategoria_IdtCategoriaOrderByNmeIdeiaAsc(codCategoria)
                .stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IdeiaResponseDTO> listarPorStatus(String status) {
        return ideiaRepository
                .findByStsIdeiaOrderByNmeIdeiaAsc(normalizar(status))
                .stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    // ---------------------- ESCRITA ----------------------

    @Transactional
    public IdeiaResponseDTO criar(IdeiaRequestDTO request) {
        TbPessoa pessoa = obterPessoa(request.codPessoa());
        TdCategoria categoria = obterCategoria(request.codCategoria());

        String status = (request.stsIdeia() == null || request.stsIdeia().isBlank())
                ? "S"
                : request.stsIdeia().trim();

        TbIdeia ideia = TbIdeia.builder()
                .pessoa(pessoa)
                .tdCategoria(categoria)
                .nmeIdeia(normalizar(request.nmeIdeia()))
                .txtResumoIdeia(normalizar(request.txtResumoIdeia()))
                .vlrOrcamentoIdeia(request.vlrOrcamentoIdeia())
                .stsIdeia(status)
                .build();

        TbIdeia salva = ideiaRepository.save(ideia);
        log.info("Ideia criada: idtIdeia={}, nome={}", salva.getIdtIdeia(), salva.getNmeIdeia());

        return IdeiaResponseDTO.from(salva);
    }

    @Transactional
    public IdeiaResponseDTO atualizar(Long id, IdeiaRequestDTO request) {
        TbIdeia ideia = obterEntidade(id);

        ideia.setPessoa(obterPessoa(request.codPessoa()));
        ideia.setTdCategoria(obterCategoria(request.codCategoria()));
        ideia.setNmeIdeia(normalizar(request.nmeIdeia()));
        ideia.setTxtResumoIdeia(normalizar(request.txtResumoIdeia()));
        ideia.setVlrOrcamentoIdeia(request.vlrOrcamentoIdeia());

        if (request.stsIdeia() != null && !request.stsIdeia().isBlank()) {
            ideia.setStsIdeia(request.stsIdeia().trim());
        }

        log.info("Ideia atualizada: idtIdeia={}", id);
        return IdeiaResponseDTO.from(ideiaRepository.save(ideia));
    }

    @Transactional
    public IdeiaResponseDTO alterarStatus(Long id, String novoStatus) {
        if (novoStatus == null || novoStatus.isBlank()) {
            throw new BusinessException("O novo status da ideia é obrigatório.");
        }

        TbIdeia ideia = obterEntidade(id);
        ideia.setStsIdeia(novoStatus.trim());

        log.info("Status da ideia alterado: idtIdeia={}, status={}", id, novoStatus);
        return IdeiaResponseDTO.from(ideiaRepository.save(ideia));
    }

    @Transactional
    public void excluir(Long id) {
        TbIdeia ideia = obterEntidade(id);

        try {
            ideiaRepository.delete(ideia);
            ideiaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha ao excluir idtIdeia={}: {}", id, e.getMostSpecificCause().getMessage());
            throw new BusinessException(
                    "Não é possível excluir a ideia: existem registros vinculados.");
        }

        log.info("Ideia excluída: idtIdeia={}", id);
    }

    // ---------------------- APOIO ----------------------

    private TbIdeia obterEntidade(Long id) {
        return ideiaRepository.findComRelacionamentos(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ideia não encontrada para o id: " + id));
    }

    private TbPessoa obterPessoa(Long codPessoa) {
        return pessoaRepository.findById(codPessoa)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pessoa não encontrada para o id: " + codPessoa));
    }

    private TdCategoria obterCategoria(Long codCategoria) {
        return categoriaRepository.findById(codCategoria)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada para o id: " + codCategoria));
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}