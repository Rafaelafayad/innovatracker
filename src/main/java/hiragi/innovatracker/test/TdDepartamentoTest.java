package hiragi.innovatracker.test;

import hiragi.innovatracker.InnovatrackerApplication;
import hiragi.innovatracker.model.TdDepartamento;
import hiragi.innovatracker.repository.TdDepartamentoRepository;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

public class TdDepartamentoTest {

    private static void incluir(TdDepartamentoRepository rep, Scanner leitor) {
        TdDepartamento tdDepartamento = new TdDepartamento();

        System.out.println("\n--- Inclusão de Departamento ---");

        System.out.print("Sigla: ");
        tdDepartamento.setSglDepartamento(leitor.nextLine());

        System.out.print("Nome: ");
        tdDepartamento.setNmeDepartamento(leitor.nextLine());

        rep.save(tdDepartamento);

        System.out.println("Departamento salvo com sucesso!");
    }

    private static void consultar(TdDepartamentoRepository rep, Scanner leitor) {
        System.out.println("\n--- Lista de Departamentos ---");

        rep.findAll().forEach(d ->
                System.out.printf(
                        "ID: %d | Sigla: %s | Nome: %s%n",
                        d.getIdtDepartamento(),
                        d.getSglDepartamento(),
                        d.getNmeDepartamento()
                )
        );
    }

    private static void excluir(TdDepartamentoRepository rep, Scanner leitor) {
        System.out.print("\nDigite o ID para excluir: ");

        Long id = leitor.nextLong();
        leitor.nextLine(); // Limpa o buffer do Enter

        if (rep.existsById(id)) {
            rep.deleteById(id);
            System.out.println("Departamento excluído com sucesso!");
        } else {
            System.out.println("Departamento não encontrado.");
        }
    }

    public static void main(String[] args) {

        try (ConfigurableApplicationContext ctx =
                     new SpringApplicationBuilder(InnovatrackerApplication.class)
                             .web(WebApplicationType.NONE)
                             .run(args)) {

            TdDepartamentoRepository rep =
                    ctx.getBean(TdDepartamentoRepository.class);

            Scanner leitor = new Scanner(System.in);

            boolean sair = false;

            while (!sair) {

                System.out.println("""
                        
                        Escolha uma das opções:
                        1 - Incluir
                        2 - Consultar
                        3 - Excluir
                        4 - Sair
                        
                        Qual a opção?
                        """);

                int opcao = leitor.nextInt();
                leitor.nextLine(); // Limpa o buffer do Enter

                switch (opcao) {
                    case 1 -> incluir(rep, leitor);
                    case 2 -> consultar(rep, leitor);
                    case 3 -> excluir(rep, leitor);
                    case 4 -> sair = true;
                    default -> System.out.println("Opção inválida!");
                }
            }

            leitor.close();
        }
    }
}
