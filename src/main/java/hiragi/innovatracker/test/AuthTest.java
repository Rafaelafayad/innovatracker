package hiragi.innovatracker.test;

import hiragi.innovatracker.InnovatrackerApplication;
import hiragi.innovatracker.dto.LoginRequestDTO;
import hiragi.innovatracker.dto.LoginResponseDTO;
import hiragi.innovatracker.dto.PessoaResumoDTO;
import hiragi.innovatracker.security.JwtTokenProvider;
import hiragi.innovatracker.service.AuthService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

public class AuthTest {

    private static void realizarLogin(AuthService authService, JwtTokenProvider jwtTokenProvider, Scanner leitor) {
        System.out.println("\n--- Teste de Autenticação / Login ---");
        System.out.print("E-mail: ");
        String email = leitor.nextLine();
        System.out.print("Senha: ");
        String senha = leitor.nextLine();

        try {
            LoginRequestDTO request = new LoginRequestDTO(email, senha);
            LoginResponseDTO response = authService.login(request);

            System.out.println("\n[SUCESSO] Login realizado com sucesso!");
            System.out.println("Tipo Token  : " + response.tipo());
            System.out.println("Token JWT   : " + response.token());
            System.out.println("Expira Em   : " + response.expiraEm() + " ms");
            System.out.println("Pessoa Logada: " + response.pessoa().nmePessoa() + " (ID: " + response.pessoa().idtPessoa() + ")");

            // Simula o endpoint /api/auth/me usando o e-mail extraído do token
            System.out.println("\n--- Teste do Endpoint /api/auth/me ---");
            String subjectEmail = jwtTokenProvider.getUsernameFromToken(response.token());
            PessoaResumoDTO me = authService.buscarPorEmail(subjectEmail);

            System.out.println("Dados do /me:");
            System.out.println("ID           : " + me.idtPessoa());
            System.out.println("Nome         : " + me.nmePessoa());
            System.out.println("E-mail       : " + me.emlPessoa());
            System.out.println("Departamento : " + me.codDepartamento());

        } catch (Exception e) {
            System.err.println("\n[ERRO] Falha ao realizar login: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InnovatrackerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {

            AuthService authService = ctx.getBean(AuthService.class);
            JwtTokenProvider jwtTokenProvider = ctx.getBean(JwtTokenProvider.class);
            Scanner leitor = new Scanner(System.in);

            boolean sair = false;
            while (!sair) {
                System.out.println("""
                        
                        Escolha uma das opções:
                        1 - Simular Login e Endpoint /me
                        2 - Sair
                        
                        Qual a opção?
                        """);
                int opcao = leitor.nextInt();
                leitor.nextLine();

                switch (opcao) {
                    case 1 -> realizarLogin(authService, jwtTokenProvider, leitor);
                    case 2 -> sair = true;
                    default -> System.out.println("Opção inválida!");
                }
            }
            leitor.close();
        }
    }
}