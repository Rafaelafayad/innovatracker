package hiragi.innovatracker.test;


import hiragi.innovatracker.InnovatrackerApplication;
import hiragi.innovatracker.model.TdCategoria;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


import java.util.Scanner;


public class TdCategoriaTest {


    // 1. Recebe o repositório como parâmetro e o Scanner reutilizado
    private static void incluir(pp rep, Scanner leitor) {
        TdCategoria tdCategoria = new TdCategoria();
        System.out.println("\n--- Inclusão de Categoria ---");


        System.out.print("Sigla: ");
        tdCategoria.setSglCategoria(leitor.nextLine());


        System.out.print("Nome: ");
        tdCategoria.setNmeCategoria(leitor.nextLine());


        System.out.print("Descrição: ");
        tdCategoria.setDscCategoria(leitor.nextLine());


        // 2. Salva no banco via Spring Data JPA
        rep.save(tdCategoria);
        System.out.println("Categoria salva com sucesso!");
    }


    private static void consultar(pp rep, Scanner leitor) {
        System.out.println("\n--- Lista de Categorias ---");
        rep.findAll().forEach(c ->
                System.out.printf("ID: %d | Sigla: %s | Nome: %s%n",
                        c.getIdtCategoria(), c.getSglCategoria(), c.getNmeCategoria())
        );
    }


    private static void excluir(pp rep, Scanner leitor) {
        System.out.print("\nDigite o ID para excluir: ");
        Long id = leitor.nextLong();
        leitor.nextLine(); // Limpa o buffer do Enter


        if (rep.existsById(id)) {
            rep.deleteById(id);
            System.out.println("Categoria excluída com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }


    public static void main(String[] args) {


        try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InnovatrackerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {


            // 3. Resgata a instância gerenciada pelo Spring
            pp rep = ctx.getBean(pp.class);


            // Scanner instanciado uma única vez fora do loop
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
                leitor.nextLine(); // Limpa o buffer do Enter após ler o int


                switch (opcao) {
                    case 1 -> incluir(rep, leitor);
                    case 2 -> consultar(rep, leitor);
                    case 3 -> excluir(rep, leitor);
                    case 4 -> sair = true;
                    default -> System.out.println("Opção inválida!");
                }
            }


            leitor.close(); // Fechado apenas ao encerrar o programa
        }
    }
}