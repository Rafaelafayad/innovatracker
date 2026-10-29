package hiragi.innovatracker.controller;


import hiragi.innovatracker.dto.AlterarSenhaDTO;
import hiragi.innovatracker.dto.PessoaRequestDTO;
import hiragi.innovatracker.dto.PessoaResponseDTO;
import hiragi.innovatracker.service.TbPessoaService;
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
@RequestMapping("/api/pessoas")
@RequiredArgsConstructor
@Tag(name = "Pessoas", description = "Endpoints para gerenciamento de cadastro de pessoas/usuários")
public class TbPessoaController {


    private final TbPessoaService pessoaService;


    @Operation(summary = "Listar pessoas paginadas", description = "Retorna uma lista paginada de pessoas ordenadas por nome.")
    @ApiResponse(responseCode = "200", description = "Lista paginada recuperada com sucesso")
    @GetMapping
    public ResponseEntity<Page<PessoaResponseDTO>> listar(
            @ParameterObject
            @PageableDefault(size = 20, sort = "nmePessoa", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(pessoaService.listar(pageable));
    }


    @Operation(summary = "Buscar pessoa por ID", description = "Recupera os detalhes de uma pessoa específica a partir do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> buscarPorId(
            @Parameter(description = "ID da pessoa", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.buscarPorId(id));
    }


    @Operation(summary = "Buscar pessoa por e-mail", description = "Recupera os detalhes de uma pessoa buscando pelo seu e-mail exato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada para o e-mail informado", content = @Content)
    })
    @GetMapping("/email")
    public ResponseEntity<PessoaResponseDTO> buscarPorEmail(
            @Parameter(description = "Endereço de e-mail exato", example = "joao@empresa.com")
            @RequestParam String valor) {
        return ResponseEntity.ok(pessoaService.buscarPorEmail(valor));
    }


    @Operation(summary = "Pesquisar pessoas por nome", description = "Realiza uma busca por correspondência parcial do nome da pessoa.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PessoaResponseDTO.class))))
    @GetMapping("/pesquisa")
    public ResponseEntity<List<PessoaResponseDTO>> pesquisarPorNome(
            @Parameter(description = "Termo de busca para o nome", example = "joa")
            @RequestParam(defaultValue = "") String nome) {
        return ResponseEntity.ok(pessoaService.pesquisarPorNome(nome));
    }


    @Operation(summary = "Listar pessoas por departamento", description = "Retorna todas as pessoas vinculadas a um determinado departamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pessoas do departamento retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado", content = @Content)
    })
    @GetMapping("/departamento/{codDepartamento}")
    public ResponseEntity<List<PessoaResponseDTO>> listarPorDepartamento(
            @Parameter(description = "Código do departamento", example = "2")
            @PathVariable Long codDepartamento) {
        return ResponseEntity.ok(pessoaService.listarPorDepartamento(codDepartamento));
    }


    @Operation(summary = "Cadastrar nova pessoa", description = "Cria um novo registro de pessoa no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI de acesso ao recurso criado")),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criar(
            @Valid @RequestBody PessoaRequestDTO request) {


        PessoaResponseDTO criada = pessoaService.criar(request);


        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.idtPessoa())
                .toUri();


        return ResponseEntity.created(location).body(criada);
    }


    @Operation(summary = "Atualizar pessoa", description = "Atualiza os dados cadastrais de uma pessoa existente. O campo de senha é opcional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> atualizar(
            @Parameter(description = "ID da pessoa", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PessoaRequestDTO request) {
        return ResponseEntity.ok(pessoaService.atualizar(id, request));
    }


    @Operation(summary = "Alterar senha", description = "Permite que a própria pessoa altere sua senha de acesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
            @Parameter(description = "ID da pessoa", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AlterarSenhaDTO request) {
        pessoaService.alterarSenha(id, request);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Alterar status da pessoa", description = "Realiza a inativação ou reativação lógica da pessoa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<PessoaResponseDTO> alterarStatus(
            @Parameter(description = "ID da pessoa", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Novo status (true para ativo, false para inativo)", example = "false")
            @RequestParam boolean ativo) {
        return ResponseEntity.ok(pessoaService.alterarStatus(id, ativo));
    }


    @Operation(summary = "Excluir pessoa", description = "Realiza a exclusão física do registro da pessoa. Falha caso existam vínculos no banco de dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pessoa excluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Exclusão não permitida devido a vínculos existentes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID da pessoa", example = "1")
            @PathVariable Long id) {
        pessoaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}