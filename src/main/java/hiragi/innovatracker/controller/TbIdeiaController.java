package hiragi.innovatracker.controller;

import hiragi.innovatracker.dto.IdeiaRequestDTO;
import hiragi.innovatracker.dto.IdeiaResponseDTO;
import hiragi.innovatracker.service.TbIdeiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ideias")
@RequiredArgsConstructor
@Tag(name = "Ideias", description = "Endpoints para gerenciamento do cadastro e fluxo de ideias")
public class TbIdeiaController {

    private final TbIdeiaService ideiaService;

    @Operation(summary = "Listar ideias paginadas", description = "Retorna uma lista paginada de ideias ordenadas por nome.")
    @ApiResponse(responseCode = "200", description = "Lista paginada recuperada com sucesso")
    @GetMapping
    public ResponseEntity<Page<IdeiaResponseDTO>> listar(
            @ParameterObject
            @PageableDefault(size = 20, sort = "nmeIdeia", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(ideiaService.listar(pageable));
    }

    @Operation(summary = "Buscar ideia por ID", description = "Recupera os detalhes de uma ideia específica a partir do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ideia encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ideia não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<IdeiaResponseDTO> buscarPorId(
            @Parameter(description = "ID da ideia", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ideiaService.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar ideias por nome", description = "Realiza uma busca por correspondência parcial do nome da ideia.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdeiaResponseDTO.class))))
    @GetMapping("/pesquisa")
    public ResponseEntity<List<IdeiaResponseDTO>> pesquisarPorNome(
            @Parameter(description = "Termo de busca para o nome", example = "plataforma")
            @RequestParam(defaultValue = "") String nome) {
        return ResponseEntity.ok(ideiaService.pesquisarPorNome(nome));
    }

    @Operation(summary = "Listar ideias por pessoa", description = "Retorna todas as ideias submetidas por uma determinada pessoa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ideias da pessoa retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @GetMapping("/pessoa/{codPessoa}")
    public ResponseEntity<List<IdeiaResponseDTO>> listarPorPessoa(
            @Parameter(description = "Código da pessoa", example = "1")
            @PathVariable Long codPessoa) {
        return ResponseEntity.ok(ideiaService.listarPorPessoa(codPessoa));
    }

    @Operation(summary = "Listar ideias por categoria", description = "Retorna todas as ideias vinculadas a uma determinada categoria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ideias da categoria retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @GetMapping("/categoria/{codCategoria}")
    public ResponseEntity<List<IdeiaResponseDTO>> listarPorCategoria(
            @Parameter(description = "Código da categoria", example = "2")
            @PathVariable Long codCategoria) {
        return ResponseEntity.ok(ideiaService.listarPorCategoria(codCategoria));
    }

    @Operation(summary = "Listar ideias por status", description = "Retorna todas as ideias filtradas pelo status informado (S, A, R ou P).")
    @ApiResponse(responseCode = "200", description = "Lista de ideias retornada com sucesso")
    @GetMapping("/status")
    public ResponseEntity<List<IdeiaResponseDTO>> listarPorStatus(
            @Parameter(description = "Código do status ('S', 'A', 'R' ou 'P')", example = "S")
            @RequestParam String status) {
        return ResponseEntity.ok(ideiaService.listarPorStatus(status));
    }

    @Operation(summary = "Cadastrar nova ideia", description = "Cria um novo registro de ideia no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ideia criada com sucesso",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI de acesso ao recurso criado")),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<IdeiaResponseDTO> criar(
            @Valid @RequestBody IdeiaRequestDTO request) {

        IdeiaResponseDTO criada = ideiaService.criar(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.idtIdeia())
                .toUri();

        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Atualizar ideia", description = "Atualiza os dados de uma ideia existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ideia atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ideia não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<IdeiaResponseDTO> atualizar(
            @Parameter(description = "ID da ideia", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody IdeiaRequestDTO request) {
        return ResponseEntity.ok(ideiaService.atualizar(id, request));
    }

    @Operation(summary = "Alterar status da ideia", description = "Altera a situação atual da ideia no fluxo de aprovação ('S', 'A', 'R' ou 'P').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status informado inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ideia não encontrada", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<IdeiaResponseDTO> alterarStatus(
            @Parameter(description = "ID da ideia", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Novo status ('S'ubmetida, 'A'provada, 'R'ejeitada, 'P'rojeto)", example = "A")
            @RequestParam String status) {
        return ResponseEntity.ok(ideiaService.alterarStatus(id, status));
    }

    @Operation(summary = "Excluir ideia", description = "Realiza a exclusão física do registro da ideia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ideia excluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Exclusão não permitida devido a vínculos existentes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ideia não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID da ideia", example = "1")
            @PathVariable Long id) {
        ideiaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}