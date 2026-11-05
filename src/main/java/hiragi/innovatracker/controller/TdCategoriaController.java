package hiragi.innovatracker.controller;

import hiragi.innovatracker.dto.CategoriaRequestDTO;
import hiragi.innovatracker.dto.CategoriaResponseDTO;
import hiragi.innovatracker.service.TdCategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
public class TdCategoriaController {


    private final TdCategoriaService categoriaService;


    @Operation(summary = "Listar categorias paginadas", description = "Retorna uma lista paginada de categorias cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada recuperada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> listar(
            @PageableDefault(size = 20, sort = "nmeCategoria", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listar(pageable));
    }


    @Operation(summary = "Listar todas as categorias", description = "Retorna uma lista simples sem paginação com todas as categorias, útil para dropdowns/combos.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista recuperada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CategoriaResponseDTO.class)))
            )
    })
    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }


    @Operation(summary = "Buscar categoria por ID", description = "Retorna os detalhes de uma categoria específica com base no seu identificador.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único da categoria", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }


    @Operation(summary = "Buscar categoria por Sigla", description = "Retorna uma categoria cadastrada a partir do texto exato da sua sigla.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @GetMapping("/sigla/{sigla}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorSigla(
            @Parameter(description = "Sigla da categoria", example = "INOV")
            @PathVariable String sigla) {
        return ResponseEntity.ok(categoriaService.buscarPorSigla(sigla));
    }


    @Operation(summary = "Pesquisar categorias por nome", description = "Busca categorias cujo nome contenha o termo informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pesquisa realizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CategoriaResponseDTO.class)))
            )
    })
    @GetMapping("/pesquisa")
    public ResponseEntity<List<CategoriaResponseDTO>> pesquisarPorNome(
            @Parameter(description = "Termo ou trecho do nome para filtragem", example = "inov")
            @RequestParam(defaultValue = "") String nome) {
        return ResponseEntity.ok(categoriaService.pesquisarPorNome(nome));
    }


    @Operation(summary = "Criar nova categoria", description = "Cadastra uma nova categoria no sistema.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria criada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(
            @Valid @RequestBody CategoriaRequestDTO request) {


        CategoriaResponseDTO criada = categoriaService.criar(request);


        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.idtCategoria())
                .toUri();


        return ResponseEntity.created(location).body(criada);
    }


    @Operation(summary = "Atualizar categoria existente", description = "Atualiza os dados de uma categoria previamente salva pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria atualizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @Parameter(description = "ID da categoria a ser atualizada", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }


    @Operation(summary = "Excluir categoria", description = "Remove uma categoria cadastrada pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID da categoria a ser removida", example = "1")
            @PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}


