package hiragi.innovatracker.controller;


import hiragi.innovatracker.service.TbPessoaRelatorioWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDate;


@RestController
@RequestMapping("/api/pessoas/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios de Pessoas", description = "Endpoints para geração e download de relatórios em formato Word (.docx)")
public class TbPessoaRelatorioController {


    private final TbPessoaRelatorioWordService pessoaRelatorioWordService;
    private static final String DOCX_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";


    @Operation(summary = "Exportar relatório geral em Word", description = "Gera um documento .docx com a listagem completa de pessoas cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório Word gerado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao gerar o arquivo Word", content = @Content)
    })
    @GetMapping("/word")
    public ResponseEntity<byte[]> exportarRelatorioGeralWord() {
        byte[] docxBytes = pessoaRelatorioWordService.gerarRelatorioGeralWord();
        String filename = "Relatorio_Geral_Pessoas_" + LocalDate.now() + ".docx";


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(DOCX_MEDIA_TYPE))
                .body(docxBytes);
    }


    @Operation(summary = "Exportar relatório por departamento em Word", description = "Gera um documento .docx contendo as pessoas filtradas por um departamento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    @GetMapping("/departamento/{codDepartamento}/word")
    public ResponseEntity<byte[]> exportarRelatorioPorDepartamentoWord(
            @Parameter(description = "Código do departamento", example = "2")
            @PathVariable Long codDepartamento) {


        byte[] docxBytes = pessoaRelatorioWordService.gerarRelatorioPorDepartamentoWord(codDepartamento);
        String filename = "Relatorio_Pessoas_Depto_" + codDepartamento + "_" + LocalDate.now() + ".docx";


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(DOCX_MEDIA_TYPE))
                .body(docxBytes);
    }
}
