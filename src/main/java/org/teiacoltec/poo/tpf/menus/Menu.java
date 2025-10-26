package org.teiacoltec.poo.tpf.menus;

import java.util.Scanner;

public abstract class Menu {
    protected Scanner scanner = new Scanner(System.in);

    protected abstract String getNomeEntidade();
    protected void criar(){}
    protected void ler(){}
    protected void atualizar(){}
    protected void deletar(){}

    public final void exibirMenu() {
        boolean continuar = true;
        do {
            System.out.println("\n--- MENU DE GERENCIAMENTO: " + getNomeEntidade().toUpperCase() + " ---");
            System.out.println("1. Criar " + getNomeEntidade());
            System.out.println("2. Listar/Consultar " + getNomeEntidade() + "s");
            System.out.println("3. Atualizar " + getNomeEntidade());
            System.out.println("4. Deletar " + getNomeEntidade());
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            int opcao = -1;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Por favor, digite um número válido.");
                continue;
            }

            switch (opcao) {
                case 1:
                    criar();
                    break;
                case 2:
                    ler();
                    break;
                case 3:
                    atualizar();
                    break;
                case 4:
                    deletar();
                    break;
                case 0:
                    continuar = false;
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (continuar);
    }


}