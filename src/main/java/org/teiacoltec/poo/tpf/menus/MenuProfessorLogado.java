package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.util.Autenticacao;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import java.util.List;
import java.util.Scanner;

public class MenuProfessorLogado {

    /**
     * Exibe o menu principal para um professor logado e processa suas escolhas.
     * @param professor O objeto Professor que está atualmente logado no sistema.
     * @param turmas A lista de todas as turmas do sistema, que pode ser modificada.
     * @param scanner Uma instância de Scanner para ler a entrada do usuário.
     * @return A lista de turmas, potencialmente modificada após as operações do professor.
     */
    public static List<Turma> exibirMenu(Professor professor, List<Turma> turmas, Scanner scanner) {
        boolean continuar = true;
        // Instancia o menuTurma que agora controla todo o fluxo de gerenciamento
        MenuTurma menuTurma = new MenuTurma(turmas);

        while (continuar) {
            System.out.println("\n====== MENU DO PROFESSOR: " + professor.getNome() + " ======");
            System.out.println("1. Criar Nova Turma");
            System.out.println("2. Gerenciar Detalhes da Turma");
            System.out.println("3. Gerenciar Atividades e Tarefas da Turma");
            System.out.println("4. Ver minhas informações");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            int opcao;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida.");
                continue;
            }

            switch (opcao) {
                case 1 -> menuTurma.criar(professor);

                // A chamada agora é simples e direta, delegando todo o controle
                case 2 -> menuTurma.iniciarGerenciamentoDetalhes(professor);
                case 3 -> menuTurma.iniciarGerenciamentoConteudos(professor);

                case 4 -> {
                    System.out.println("\n--- Suas Informações ---");
                    System.out.println(professor);
                }
                case 0 -> {
                    continuar = false;
                    Autenticacao.logout(professor);
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
        return turmas;
    }
}