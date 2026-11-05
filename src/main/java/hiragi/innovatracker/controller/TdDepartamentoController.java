package hiragi.innovatracker.controller;


import hiragi.innovatracker.dto.DepartamentoRequestDTO;
import hiragi.innovatracker.dto.DepartamentoResponseDTO;
import hiragi.innovatracker.service.TdDepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
@Tag(name = "Departamentos", description = "Endpoints para gerenciamento de departamentos")
public class TdDepartamentoController {


    private final TdDepartamentoService departamentoService;


    @Operation(summary = "Listar departamentos paginados", description = "Retorna uma lista paginada de departamentos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada recuperada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<DepartamentoResponseDTO>> listar(
            @ParameterObject
            @PageableDefault(size = 20, sort = "nmeDepartamento", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(departamentoService.listar(pageable));
    }


    @Operation(summary = "Listar todos os departamentos", description = "Retorna uma lista simples sem paginação com todos os departamentos, útil para preencher combos e selects.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista recuperada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = DepartamentoResponseDTO.class)))
            )
    })
    @GetMapping("/todos")
    public ResponseEntity<List<DepartamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(departamentoService.listarTodos());
    }


    @Operation(summary = "Buscar departamento por ID", description = "Retorna os detalhes de um departamento específico com base no seu identificador.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Departamento encontrado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DepartamentoResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único do departamento", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(departamentoService.buscarPorId(id));
    }


    @Operation(summary = "Buscar departamento por Sigla", description = "Retorna um departamento cadastrado a partir do texto exato da sua sigla.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Departamento encontrado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DepartamentoResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    @GetMapping("/sigla/{sigla}")
    public ResponseEntity<DepartamentoResponseDTO> buscarPorSigla(
            @Parameter(description = "Sigla do departamento", example = "TI")
            @PathVariable String sigla) {
        return ResponseEntity.ok(departamentoService.buscarPorSigla(sigla));
    }


    @Operation(summary = "Pesquisar departamentos por nome", description = "Busca departamentos cujo nome contenha o termo informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pesquisa realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = DepartamentoResponseDTO.class)))
            )
    })
    @GetMapping("/pesquisa")
    public ResponseEntity<List<DepartamentoResponseDTO>> pesquisarPorNome(
            @Parameter(description = "Termo ou trecho do nome para filtragem", example = "tecnologia")
            @RequestParam(defaultValue = "") String nome) {
        return ResponseEntity.ok(departamentoService.pesquisarPorNome(nome));
    }


    @Operation(summary = "Criar novo departamento", description = "Cadastra um novo departamento no sistema.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Departamento criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DepartamentoResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DepartamentoResponseDTO> criar(
            @Valid @RequestBody DepartamentoRequestDTO request) {
        DepartamentoResponseDTO criado = departamentoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.idtDepartamento())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }


    @Operation(summary = "Atualizar departamento existente", description = "Atualiza os dados de um departamento previamente salvo pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Departamento atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DepartamentoResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> atualizar(
            @Parameter(description = "ID do departamento a ser atualizado", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody DepartamentoRequestDTO request) {
        return ResponseEntity.ok(departamentoService.atualizar(id, request));
    }


    @Operation(summary = "Excluir departamento", description = "Remove um departamento cadastrado pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Departamento excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do departamento a ser removido", example = "1")
            @PathVariable Long id) {
        departamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
