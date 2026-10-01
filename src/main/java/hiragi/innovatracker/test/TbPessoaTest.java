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
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

public class TbPessoaTest {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static void incluir(
            TbPessoaRepository pessoaRep,
            TdDepartamentoRepository departamentoRep,
            Scanner leitor) {

        System.out.println("\n--- Inclusão de Pessoa ---");

        System.out.print("ID do Departamento: ");
        Long idDepartamento = leitor.nextLong();
        leitor.nextLine();

        Optional<TdDepartamento> departamentoOpt =
                departamentoRep.findById(idDepartamento);

        if (departamentoOpt.isEmpty()) {
            System.out.println("Departamento não encontrado!");
            System.out.println("Operação cancelada.");
            return;
        }

        TbPessoa pessoa = new TbPessoa();

        pessoa.setTdDepartamento(departamentoOpt.get());

        System.out.print("Nome: ");
        pessoa.setNmePessoa(leitor.nextLine());

        System.out.print("E-mail: ");
        String email = leitor.nextLine();

        if (pessoaRep.existsByEmlPessoaIgnoreCase(email)) {
            System.out.println("Este e-mail já está cadastrado!");
            System.out.println("Operação cancelada.");
            return;
        }

        pessoa.setEmlPessoa(email);

        System.out.print("Senha: ");
        pessoa.setPwdPessoa(leitor.nextLine());

        System.out.print("Data de nascimento (dd/MM/yyyy): ");

        try {
            LocalDate dataNascimento =
                    LocalDate.parse(leitor.nextLine(), FORMATO_DATA);

            pessoa.setDtaNascimentoPessoa(dataNascimento);

        } catch (DateTimeParseException e) {
            System.out.println(
                    "Data inválida! Use o formato dd/MM/yyyy."
            );
            return;
        }

        System.out.print("Pessoa ativa? (S/N): ");
        String ativo = leitor.nextLine().trim().toUpperCase();

        pessoa.setFlgAtivoPessoa(ativo.equals("S"));

        pessoaRep.save(pessoa);

        System.out.println("Pessoa salva com sucesso!");
    }

    private static void consultar(TbPessoaRepository pessoaRep) {

        System.out.println("\n--- Lista de Pessoas ---");

        pessoaRep.findAll().forEach(p ->
                System.out.printf(
                        "ID: %d | Nome: %s | E-mail: %s | " +
                                "Nascimento: %s | Ativo: %s%n",

                        p.getIdtPessoa(),
                        p.getNmePessoa(),
                        p.getEmlPessoa(),
                        p.getDtaNascimentoPessoa()
                                .format(FORMATO_DATA),
                        p.getFlgAtivoPessoa() ? "Sim" : "Não"
                )
        );
    }

    private static void excluir(
            TbPessoaRepository pessoaRep,
            Scanner leitor) {

        System.out.print("\nDigite o ID da Pessoa para excluir: ");

        Long id = leitor.nextLong();
        leitor.nextLine();

        if (pessoaRep.existsById(id)) {

            pessoaRep.deleteById(id);

            System.out.println(
                    "Pessoa excluída com sucesso!"
            );

        } else {

            System.out.println(
                    "Pessoa não encontrada."
            );
        }
    }

    public static void main(String[] args) {

        try (ConfigurableApplicationContext ctx =
                     new SpringApplicationBuilder(
                             InnovatrackerApplication.class)
                             .web(WebApplicationType.NONE)
                             .run(args)) {

            TbPessoaRepository pessoaRep =
                    ctx.getBean(TbPessoaRepository.class);

            TdDepartamentoRepository departamentoRep =
                    ctx.getBean(TdDepartamentoRepository.class);

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
                leitor.nextLine();

                switch (opcao) {

                    case 1 ->
                            incluir(
                                    pessoaRep,
                                    departamentoRep,
                                    leitor
                            );

                    case 2 ->
                            consultar(pessoaRep);

                    case 3 ->
                            excluir(
                                    pessoaRep,
                                    leitor
                            );

                    case 4 ->
                            sair = true;

                    default ->
                            System.out.println(
                                    "Opção inválida!"
                            );
                }
            }

            leitor.close();
        }
    }
}