package hiragi.innovatracker.controller;


import hiragi.innovatracker.dto.ThreadStatusDTO;
import hiragi.innovatracker.security.JwtTokenProvider;
import hiragi.innovatracker.service.ReportGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/diagnostic")
@Tag(name = "Diagnóstico do Sistema", description = "Endpoints para verificação do InnovaTracker, Virtual Threads, JWT e Apache POI.")
@SecurityRequirements // Avisa ao Swagger que os endpoints desta controller são públicos (sem cadeado JWT)
public class DiagnosticController {


    private final JwtTokenProvider jwtTokenProvider;
    private final ReportGeneratorService reportGeneratorService;


    public DiagnosticController(JwtTokenProvider jwtTokenProvider, ReportGeneratorService reportGeneratorService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.reportGeneratorService = reportGeneratorService;
    }


    @GetMapping("/thread-info")
    @Operation(summary = "Inspeciona a thread atual no InnovaTracker para comprovar o uso de Virtual Threads")
    public ResponseEntity<ThreadStatusDTO> getThreadInfo() {
        return ResponseEntity.ok(ThreadStatusDTO.captureCurrent());
    }


    @PostMapping("/generate-test-token")
    @Operation(summary = "Gera um token JWT assinado para testes de autenticação")
    public ResponseEntity<Map<String, String>> generateTestToken(@RequestParam String username) {
        String token = jwtTokenProvider.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "Gera e faz o download de uma planilha Excel de teste usando Apache POI")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        byte[] excelContent = reportGeneratorService.generateSampleExcelReport();


        // Mime-type correto para arquivos .xlsx
        MediaType excelMediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


        // Uso do ContentDisposition seguro do Spring
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("innovatracker-diagnostico.xlsx")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(excelMediaType)
                .body(excelContent);
    }
}
