package hiragi.innovatracker.test;

import hiragi.innovatracker.InnovatrackerApplication;
import hiragi.innovatracker.model.TbPessoa;
import hiragi.innovatracker.model.TdDepartamento;
import hiragi.innovatracker.repository.TbPessoaRepository;
import hiragi.innovatracker.repository.TdDepartamentoRepository;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

public class TbPessoaTest {


    private static void incluir(TbPessoaRepository pessoaRep,
                                TdDepartamentoRepository departamentoRep,
                                Scanner leitor) {


        System.out.println("\n--- Inclusão de Pessoa ---");


        // 1. Validar/Buscar Departamento
        System.out.print("ID do Departamento que a pessoa trabalha: ");
        Long idDepartamento = leitor.nextLong();
        leitor.nextLine(); // Limpa buffer

        Optional<TdDepartamento> departamentoOpt = departamentoRep.findById(idDepartamento);
        if (departamentoOpt.isEmpty()) {
            System.out.println("Departamento não encontrado! Operação cancelada.");
            return;
        }

        // 2. Preencher os dados de departamento
        TbPessoa pessoa = new TbPessoa();
        pessoa.setTdDepartamento(departamentoOpt.get());

        System.out.print("Nome da Pessoa: ");
        pessoa.setNmePessoa(leitor.nextLine());

        System.out.print("Email da Pessoa: ");
        pessoa.setEmlPessoa(leitor.nextLine());

        System.out.print("Senha da Pessoa: ");
        pessoa.setPwdPessoa(leitor.nextLine());

        System.out.print("Data de Nascimento: dd/MM/yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String textoData = leitor.nextLine();
        LocalDate dtaNascimento = LocalDate.parse(textoData, formatter);
        pessoa.setDtaNascimentoPessoa(dtaNascimento);

        // 4. Salvar
        pessoaRep.save(pessoa);
        System.out.println("Pessoa salva com sucesso!");
    }


    private static void consultar(TbPessoaRepository pessoaRep) {
        System.out.println("\n--- Lista de Pessoa ---");
        pessoaRep.findAll().forEach(p ->
                System.out.printf("ID: %d | Nome: %s | Email: %s%nData de Nascimento: %s | Departamento: %s%n",
                        p.getIdtPessoa(), p.getNmePessoa(), p.getEmlPessoa(), p.getDtaNascimentoPessoa(),
                        p.getTdDepartamento().getNmeDepartamento())
        );
    }


    private static void excluir(TbPessoaRepository pessoaRep, Scanner leitor) {
        System.out.print("\nDigite o ID da Pessoa para excluir: ");
        Long id = leitor.nextLong();
        leitor.nextLine(); // Limpa buffer


        if (pessoaRep.existsById(id)) {
            pessoaRep.deleteById(id);
            System.out.println("Pessoa excluída com sucesso!");
        } else {
            System.out.println("Pessoa não encontrada.");
        }
    }


    public static void main(String[] args) {


        try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(InnovatrackerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {


            // Resgata os Beans dos repositórios
            TbPessoaRepository pessoaRep = ctx.getBean(TbPessoaRepository.class);
            TdDepartamentoRepository departamentoRep = ctx.getBean(TdDepartamentoRepository.class);

            Scanner leitor = new Scanner(System.in);
            boolean sair = false;

            while (!sair) {
                System.out.println("""
                      
                       Escolha uma das opções:
                       1 - Incluir Pessoa
                       2 - Consultar Pessoas
                       3 - Excluir Pessoa
                       4 - Sair
                      
                       Qual a opção?
                       """);


                int opcao = leitor.nextInt();
                leitor.nextLine(); // Limpa buffer


                switch (opcao) {
                    case 1 -> incluir(pessoaRep, departamentoRep, leitor);
                    case 2 -> consultar(pessoaRep);
                    case 3 -> excluir(pessoaRep, leitor);
                    case 4 -> sair = true;
                    default -> System.out.println("Opção inválida!");
                }
            }


            leitor.close();
        }
    }
}
