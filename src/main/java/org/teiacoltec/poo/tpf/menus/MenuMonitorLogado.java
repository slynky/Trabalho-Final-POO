package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.util.Autenticacao;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Define a interface de menu para um monitor que já realizou o login no sistema.
 * Apresenta opções específicas para as ações que um monitor pode realizar, como
 * consultar informações da turma e listar seus participantes.
 */
public class MenuMonitorLogado {

    /**
     * Exibe o menu principal do monitor e processa as escolhas do usuário.
     * O método opera em um loop até que o monitor escolha a opção de logout.
     * @param monitor O objeto do tipo Monitor que está atualmente logado.
     * @param turmas A lista completa de turmas do sistema.
     * @param scanner Uma instância do Scanner para capturar a entrada do usuário.
     */
    public static void exibirMenu(Monitor monitor, List<Turma> turmas, Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n====== MENU DO MONITOR: " + monitor.getNome() + " ======");
            System.out.println("1. Ver minhas informações");
            System.out.println("2. Ver informações das minhas turmas");
            System.out.println("3. Listar alunos da turma");
            System.out.println("4. Ver atividades da turma");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            int opcao = -1;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Por favor, digite um número válido.");
                continue;
            }

            // Busca a turma do monitor uma única vez para usar nas opções do menu.
            Optional<Turma> turmaDoMonitor = turmas.stream()
                    .filter(turma -> turma.isParticipante(monitor.getCpf()))
                    .findFirst();

            switch (opcao) {
                case 1:
                    System.out.println("\n--- Suas Informações ---");
                    System.out.println(monitor.obterInformacoes());
                    break;
                case 2:
                    System.out.println("\n--- Informações da Turma ---");
                    turmaDoMonitor.ifPresentOrElse(
                            turma -> System.out.println(turma.toString()),
                            () -> System.out.println("Você não está associado a nenhuma turma.")
                    );
                    break;
                case 3:
                    System.out.println("\n--- Alunos da Turma ---");
                    turmaDoMonitor.ifPresentOrElse(
                            MenuMonitorLogado::listarAlunosDaTurma, // Usa um metodo de referência
                            () -> System.out.println("Não foi possível listar alunos pois você não está em uma turma.")
                    );
                    break;
                case 4:
                    System.out.println("\n--- Atividades da Turma ---");
                    turmaDoMonitor.ifPresentOrElse(
                            turma -> {
                                if (turma.getAtividades().isEmpty()) {
                                    System.out.println("Nenhuma atividade cadastrada para esta turma.");
                                } else {
                                    turma.getAtividades().forEach(System.out::println);
                                }
                            },
                            () -> System.out.println("Não foi possível listar atividades pois você não está em uma turma.")
                    );
                    break;
                case 0:
                    continuar = false;
                    Autenticacao.logout(monitor);
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    /**
     * Método auxiliar para listar apenas os alunos de uma determinada turma.
     * @param turma A turma cujos alunos serão listados.
     */
    private static void listarAlunosDaTurma(Turma turma) {
        // Filtra a lista de participantes para obter apenas os que são instâncias de Aluno.
        List<Aluno> alunos = turma.getParticipantes().stream()
                .filter(p -> p instanceof Aluno)
                .map(p -> (Aluno) p)
                .toList(); // .toList() é uma forma mais moderna de .collect(Collectors.toList()) (Java 16+)

        if (alunos.isEmpty()) {
            System.out.println("A turma '" + turma.getNome() + "' não possui alunos cadastrados.");
        } else {
            alunos.forEach(aluno ->
                    System.out.printf("- %s (Matrícula: %s)\n", aluno.getNome(), aluno.getMatricula())
            );
        }
    }
}