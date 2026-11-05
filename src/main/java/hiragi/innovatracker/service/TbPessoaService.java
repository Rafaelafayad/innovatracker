package hiragi.innovatracker.service;

import hiragi.innovatracker.dto.AlterarSenhaDTO;
import hiragi.innovatracker.dto.PessoaRequestDTO;
import hiragi.innovatracker.dto.PessoaResponseDTO;
import hiragi.innovatracker.exception.BusinessException;
import hiragi.innovatracker.exception.ResourceNotFoundException;
import hiragi.innovatracker.model.TbPessoa;
import hiragi.innovatracker.model.TdDepartamento;
import hiragi.innovatracker.repository.TbPessoaRepository;
import hiragi.innovatracker.repository.TdDepartamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TbPessoaService {
    private final TbPessoaRepository pessoaRepository;
    private final TdDepartamentoRepository departamentoRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------------------- LEITURA ----------------------
    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> listar(Pageable pageable) {
        return pessoaRepository.findAll(pageable)
                .map(PessoaResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorId(Long id) {
        return PessoaResponseDTO.from(obterEntidade(id));
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorEmail(String email) {
        String valor = normalizar(email);
        return pessoaRepository.findComDepartamentoPorEmail(valor)
                .map(PessoaResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pessoa não encontrada para o e-mail: " + valor));
    }


    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> pesquisarPorNome(String nome) {
        return pessoaRepository
                .findByNmePessoaContainingIgnoreCaseOrderByNmePessoaAsc(normalizar(nome))
                .stream()
                .map(PessoaResponseDTO::from)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> listarPorDepartamento(Long codDepartamento) {
        return pessoaRepository
                .findByTdDepartamento_IdtDepartamentoOrderByNmePessoaAsc(codDepartamento)
                .stream()
                .map(PessoaResponseDTO::from)
                .toList();
    }


    // ---------------------- ESCRITA ----------------------


    @Transactional
    public PessoaResponseDTO criar(PessoaRequestDTO request) {
        String email = normalizar(request.emlPessoa()).toLowerCase();

        if (request.senha() == null || request.senha().isBlank()) {
            throw new BusinessException("A senha é obrigatória na criação da pessoa.");
        }

        if (pessoaRepository.existsByEmlPessoaIgnoreCase(email)) {
            throw new BusinessException("Já existe uma pessoa com o e-mail: " + email);
        }
        TdDepartamento departamento = obterDepartamento(request.codDepartamento());

        TbPessoa pessoa = TbPessoa.builder()
                .tdDepartamento(departamento)
                .nmePessoa(normalizar(request.nmePessoa()))
                .emlPessoa(email)
                .pwdPessoa(passwordEncoder.encode(request.senha()))
                .dtaNascimentoPessoa(request.dtaNascimentoPessoa())
                .flgAtivoPessoa(request.flgAtivoPessoa() == null || request.flgAtivoPessoa())
                .build();


        TbPessoa salva = pessoaRepository.save(pessoa);
        log.info("Pessoa criada: idtPessoa={}, email={}", salva.getIdtPessoa(), email);


        return PessoaResponseDTO.from(salva);
    }




    @Transactional
    public PessoaResponseDTO atualizar(Long id, PessoaRequestDTO request) {
        TbPessoa pessoa = obterEntidade(id);

        String email = normalizar(request.emlPessoa()).toLowerCase();
        validarEmailDuplicado(email, id);

        pessoa.setTdDepartamento(obterDepartamento(request.codDepartamento()));
        pessoa.setNmePessoa(normalizar(request.nmePessoa()));
        pessoa.setEmlPessoa(email);
        pessoa.setDtaNascimentoPessoa(request.dtaNascimentoPessoa());

        if (request.flgAtivoPessoa() != null) {
            pessoa.setFlgAtivoPessoa(request.flgAtivoPessoa());
        }

        // Senha só é alterada se vier preenchida
        if (request.senha() != null && !request.senha().isBlank()) {
            pessoa.setPwdPessoa(passwordEncoder.encode(request.senha()));
            log.info("Senha redefinida para idtPessoa={}", id);
        }

        log.info("Pessoa atualizada: idtPessoa={}", id);
        return PessoaResponseDTO.from(pessoaRepository.save(pessoa));
    }


    @Transactional
    public void alterarSenha(Long id, AlterarSenhaDTO request) {
        TbPessoa pessoa = obterEntidade(id);

        if (!passwordEncoder.matches(request.senhaAtual(), pessoa.getPwdPessoa())) {
            throw new BusinessException("A senha atual está incorreta.");
        }
        if (passwordEncoder.matches(request.senhaNova(), pessoa.getPwdPessoa())) {
            throw new BusinessException("A nova senha deve ser diferente da atual.");
        }


        pessoa.setPwdPessoa(passwordEncoder.encode(request.senhaNova()));
        pessoaRepository.save(pessoa);


        log.info("Senha alterada pelo próprio usuário: idtPessoa={}", id);
    }


    /** Inativa em vez de excluir — preserva o histórico de registros vinculados. */
    @Transactional
    public PessoaResponseDTO alterarStatus(Long id, boolean ativo) {
        TbPessoa pessoa = obterEntidade(id);
        pessoa.setFlgAtivoPessoa(ativo);

        log.info("Status alterado: idtPessoa={}, ativo={}", id, ativo);
        return PessoaResponseDTO.from(pessoaRepository.save(pessoa));
    }


    @Transactional
    public void excluir(Long id) {
        TbPessoa pessoa = obterEntidade(id);

        try {
            pessoaRepository.delete(pessoa);
            pessoaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha ao excluir idtPessoa={}: {}", id, e.getMostSpecificCause().getMessage());
            throw new BusinessException(
                    "Não é possível excluir a pessoa: existem registros vinculados. Considere inativá-la.");
        }

        log.info("Pessoa excluída: idtPessoa={}", id);
    }


    // ---------------------- APOIO ----------------------

    private TbPessoa obterEntidade(Long id) {
        return pessoaRepository.findComDepartamento(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pessoa não encontrada para o id: " + id));
    }

    private TdDepartamento obterDepartamento(Long codDepartamento) {
        return departamentoRepository.findById(codDepartamento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departamento não encontrado para o id: " + codDepartamento));
    }

    private void validarEmailDuplicado(String email, Long idAtual) {
        pessoaRepository.findByEmlPessoaIgnoreCase(email)
                .filter(existente -> !existente.getIdtPessoa().equals(idAtual))
                .ifPresent(existente -> {
                    throw new BusinessException("Já existe uma pessoa com o e-mail: " + email);
                });
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
