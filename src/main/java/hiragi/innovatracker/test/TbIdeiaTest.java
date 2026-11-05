package hiragi.innovatracker.test;

import hiragi.innovatracker.InnovatrackerApplication;
import hiragi.innovatracker.model.TbIdeia;
import hiragi.innovatracker.model.TbPessoa;
import hiragi.innovatracker.model.TdCategoria;
import hiragi.innovatracker.repository.TbIdeiaRepository;
import hiragi.innovatracker.repository.TbPessoaRepository;
import hiragi.innovatracker.repository.TdCategoriaRepository;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

public class TbIdeiaTest {


    private static void incluir(TbIdeiaRepository ideiaRep,
                                TdCategoriaRepository categoriaRep,
                                TbPessoaRepository pessoaRep,
                                Scanner leitor) {


        System.out.println("\n--- Inclusão de Ideia ---");


        // 1. Validar/Buscar a Pessoa
        System.out.print("ID da Pessoa responsável: ");
        Long idPessoa = leitor.nextLong();
        leitor.nextLine(); // Limpa buffer


        Optional<TbPessoa> pessoaOpt = pessoaRep.findById(idPessoa);
        if (pessoaOpt.isEmpty()) {
            System.out.println("Pessoa não encontrada! Operação cancelada.");
            return;
        }


        // 2. Validar/Buscar a Categoria
        System.out.print("ID da Categoria: ");
        Long idCategoria = leitor.nextLong();
        leitor.nextLine(); // Limpa buffer


        Optional<TdCategoria> categoriaOpt = categoriaRep.findById(idCategoria);
        if (categoriaOpt.isEmpty()) {
            System.out.println("Categoria não encontrada! Operação cancelada.");
            return;
        }


        // 3. Preencher os dados da Ideia
        TbIdeia ideia = new TbIdeia();
        ideia.setPessoa(pessoaOpt.get());
        ideia.setTdCategoria(categoriaOpt.get());


        System.out.print("Nome da Ideia: ");
        ideia.setNmeIdeia(leitor.nextLine());


        System.out.print("Resumo: ");
        ideia.setTxtResumoIdeia(leitor.nextLine());


        System.out.print("Valor do Orçamento: ");
        ideia.setVlrOrcamentoIdeia(new BigDecimal(leitor.nextLine()));


        System.out.print("Status ('S'ubmetida, 'A'provada, 'R'ejeitada, 'P'rojeto): ");
        ideia.setStsIdeia(leitor.nextLine().toUpperCase());


        // 4. Salvar
        ideiaRep.save(ideia);
        System.out.println("Ideia salva com sucesso!");
    }


    private static void consultar(TbIdeiaRepository ideiaRep) {
        System.out.println("\n--- Lista de Ideias ---");
        ideiaRep.findAll().forEach(i ->
                System.out.printf("ID: %d | Categoria: %s | Pessoa: %s%nNome Ideia: %s | Orçamento: R$ %.2f | Status: %s%n",
                        i.getIdtIdeia(), i.getTdCategoria().getNmeCategoria(),
                        i.getPessoa().getNmePessoa(), i.getNmeIdeia(), i.getVlrOrcamentoIdeia(), i.getStsIdeia())
        );
    }


    private static void excluir(TbIdeiaRepository ideiaRep, Scanner leitor) {
        System.out.print("\nDigite o ID da Ideia para excluir: ");
        Long id = leitor.nextLong();
        leitor.nextLine(); // Limpa buffer


        if (ideiaRep.existsById(id)) {
            ideiaRep.deleteById(id);
            System.out.println("Ideia excluída com sucesso!");
        } else {
            System.out.println("Ideia não encontrada.");
        }
    }


    public static void main(String[] args) {


        try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InnovatrackerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {


            // Resgata os Beans dos repositórios
            TbIdeiaRepository ideiaRep = ctx.getBean(TbIdeiaRepository.class);
            TdCategoriaRepository categoriaRep = ctx.getBean(TdCategoriaRepository.class);
            TbPessoaRepository pessoaRep = ctx.getBean(TbPessoaRepository.class);

            Scanner leitor = new Scanner(System.in);
            boolean sair = false;

            while (!sair) {
                System.out.println("""
                      
                       Escolha uma das opções:
                       1 - Incluir Ideia
                       2 - Consultar Ideias
                       3 - Excluir Ideia
                       4 - Sair
                      
                       Qual a opção?
                       """);


                int opcao = leitor.nextInt();
                leitor.nextLine(); // Limpa buffer


                switch (opcao) {
                    case 1 -> incluir(ideiaRep, categoriaRep, pessoaRep, leitor);
                    case 2 -> consultar(ideiaRep);
                    case 3 -> excluir(ideiaRep, leitor);
                    case 4 -> sair = true;
                    default -> System.out.println("Opção inválida!");
                }
            }


            leitor.close();
        }
    }
}
