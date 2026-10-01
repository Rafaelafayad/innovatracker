package hiragi.innovatracker.test;

import hiragi.innovatracker.InnovatrackerApplication;
import hiragi.innovatracker.model.TdCategoria;
import hiragi.innovatracker.repository.TdCategoriaRepository;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

public class TdCategoriaTest {

    private static void incluir(
            TdCategoriaRepository rep,
            Scanner leitor) {

        TdCategoria tdCategoria = new TdCategoria();

        System.out.println("\n--- Inclusão de Categoria ---");

        System.out.print("Sigla: ");
        tdCategoria.setSglCategoria(leitor.nextLine());

        System.out.print("Nome: ");
        tdCategoria.setNmeCategoria(leitor.nextLine());

        System.out.print("Descrição: ");
        tdCategoria.setDscCategoria(leitor.nextLine());

        rep.save(tdCategoria);

        System.out.println("Categoria salva com sucesso!");
    }

    private static void consultar(TdCategoriaRepository rep) {

        System.out.println("\n--- Lista de Categorias ---");

        rep.findAll().forEach(c ->
                System.out.printf(
                        "ID: %d | Sigla: %s | Nome: %s | Descrição: %s%n",
                        c.getIdtCategoria(),
                        c.getSglCategoria(),
                        c.getNmeCategoria(),
                        c.getDscCategoria()
                )
        );
    }

    private static void excluir(
            TdCategoriaRepository rep,
            Scanner leitor) {

        System.out.print("\nDigite o ID para excluir: ");

        Long id = leitor.nextLong();
        leitor.nextLine(); // Limpa o buffer

        if (rep.existsById(id)) {
            rep.deleteById(id);
            System.out.println("Categoria excluída com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    public static void main(String[] args) {

        try (ConfigurableApplicationContext ctx =
                     new SpringApplicationBuilder(InnovatrackerApplication.class)
                             .web(WebApplicationType.NONE)
                             .run(args)) {

            TdCategoriaRepository rep =
                    ctx.getBean(TdCategoriaRepository.class);

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
                leitor.nextLine(); // Limpa o buffer

                switch (opcao) {
                    case 1 -> incluir(rep, leitor);
                    case 2 -> consultar(rep);
                    case 3 -> excluir(rep, leitor);
                    case 4 -> sair = true;
                    default -> System.out.println("Opção inválida!");
                }
            }

            leitor.close();
        }
    }
}