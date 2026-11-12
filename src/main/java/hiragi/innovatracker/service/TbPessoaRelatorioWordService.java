package hiragi.innovatracker.service;


import hiragi.innovatracker.dto.PessoaResponseDTO;
import hiragi.innovatracker.repository.TbPessoaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TbPessoaRelatorioWordService {


    private final TbPessoaRepository pessoaRepository;
    private final RelatorioPessoaWordService relatorioPessoaWordService;


    @Transactional(readOnly = true)
    public byte[] gerarRelatorioGeralWord() {
        log.info("Buscando todos os registros de pessoas para o relatório Word...");


        List<PessoaResponseDTO> pessoas = pessoaRepository.findAll(Sort.by(Sort.Direction.ASC, "nmePessoa"))
                .stream()
                .map(PessoaResponseDTO::from)
                .toList();


        return relatorioPessoaWordService.gerarRelatorioPessoas(pessoas, "Relatório Geral de Pessoas");
    }


    @Transactional(readOnly = true)
    public byte[] gerarRelatorioPorDepartamentoWord(Long codDepartamento) {
        log.info("Buscando pessoas do departamento codDepartamento={} para relatório Word...", codDepartamento);


        List<PessoaResponseDTO> pessoas = pessoaRepository
                .findByTdDepartamento_IdtDepartamentoOrderByNmePessoaAsc(codDepartamento)
                .stream()
                .map(PessoaResponseDTO::from)
                .toList();


        String titulo = "Relatório de Pessoas - Departamento " + codDepartamento;
        return relatorioPessoaWordService.gerarRelatorioPessoas(pessoas, titulo);
    }
}
