package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.util.Autenticacao;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Define a interface de menu para um aluno que já realizou o login no sistema.
 * Apresenta opções específicas para as ações que um aluno pode realizar.
 */
public class MenuAlunoLogado {

    /**
     * Exibe o menu principal do aluno e processa as escolhas do usuário.
     * @param aluno O objeto Aluno que está atualmente logado.
     * @param todasAsTurmas A lista completa de turmas do sistema.
     * @param scanner Uma instância do Scanner para capturar a entrada do usuário.
     */
    public static void exibirMenu(Aluno aluno, List<Turma> todasAsTurmas, Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n====== MENU DO ALUNO: " + aluno.getNome() + " ======");
            System.out.println("1. Visualizar Minhas Turmas");
            System.out.println("2. Ver minhas informações pessoais");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            int opcao;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Por favor, digite um número válido.");
                continue;
            }

            switch (opcao) {
                case 1 -> visualizarMinhasTurmas(aluno, todasAsTurmas, scanner);
                case 2 -> {
                    System.out.println("\n--- Suas Informações Pessoais ---");
                    System.out.println(aluno); // O método obterInformacoes() pode ser usado aqui também
                }
                case 0 -> {
                    continuar = false;
                    Autenticacao.logout(aluno);
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    /**
     * Filtra e exibe as turmas do aluno, permitindo que ele selecione uma para ver os detalhes.
     * @param aluno O aluno logado.
     * @param todasAsTurmas A lista completa de turmas do sistema.
     * @param scanner Scanner para entrada do usuário.
     */
    private static void visualizarMinhasTurmas(Aluno aluno, List<Turma> todasAsTurmas, Scanner scanner) {
        // Filtra todas as turmas do sistema para encontrar apenas aquelas em que o aluno participa.
        List<Turma> minhasTurmas = todasAsTurmas.stream()
                .filter(turma -> turma.participa(aluno))
                .collect(Collectors.toList());

        if (minhasTurmas.isEmpty()) {
            System.out.println("\nVocê não está matriculado em nenhuma turma no momento.");
            return;
        }

        Turma turmaSelecionada;
        // Se o aluno está em apenas uma turma, entra nela diretamente.
        if (minhasTurmas.size() == 1) {
            turmaSelecionada = minhasTurmas.get(0);
        } else {
            // Se estiver em várias, pede para ele escolher.
            System.out.println("\n--- Você está em " + minhasTurmas.size() + " turmas. Selecione uma: ---");
            minhasTurmas.forEach(t -> System.out.println("ID: " + t.getId() + " | Nome: " + t.getNome()));
            System.out.print("\nDigite o ID da turma que deseja visualizar (ou 0 para voltar): ");

            int idTurma;
            try {
                idTurma = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ID inválido.");
                return;
            }
            if (idTurma == 0) return;

            Optional<Turma> turmaOpt = minhasTurmas.stream().filter(t -> t.getId() == idTurma).findFirst();
            if (turmaOpt.isEmpty()) {
                System.out.println("ID não corresponde a nenhuma de suas turmas.");
                return;
            }
            turmaSelecionada = turmaOpt.get();
        }

        // Exibe o menu detalhado para a turma escolhida.
        exibirMenuDaTurma(turmaSelecionada, scanner);
    }

    /**
     * Exibe um menu detalhado e de apenas leitura para uma turma específica.
     * @param turma A turma selecionada pelo aluno.
     * @param scanner Scanner para entrada do usuário.
     */
    private static void exibirMenuDaTurma(Turma turma, Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n====== VISUALIZANDO TURMA: " + turma.getNome() + " ======");
            System.out.println("1. Ver Participantes da Turma");
            System.out.println("2. Ver Atividades da Turma");
            System.out.println("3. Ver Minhas Notas");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            int opcao;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Por favor, digite um número válido.");
                continue;
            }

            switch (opcao) {
                case 1 -> {
                    System.out.println("\n--- Participantes da Turma ---");
                    turma.listarParticipantes(); // Reutiliza o método da classe Turma
                }
                case 2 -> {
                    System.out.println("\n--- Atividades da Turma ---");
                    if (turma.getAtividades().isEmpty()) {
                        System.out.println("Nenhuma atividade cadastrada para esta turma.");
                    } else {
                        turma.getAtividades().forEach(System.out::println);
                    }
                }
                case 3 -> System.out.println("\n(Funcionalidade de visualizar notas ainda não implementada.)");
                case 0 -> continuar = false;
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}