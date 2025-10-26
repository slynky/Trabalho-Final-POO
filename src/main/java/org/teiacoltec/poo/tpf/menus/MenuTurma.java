package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.exceptions.AcessoNaoAutorizadoException;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuTurma extends Menu {
    private final List<Turma> turmas;
    private static int proximoId;

    /**
     * Construtor da classe MenuTurma.
     * @param turmas A lista de turmas do sistema na qual as operações serão realizadas.
     */
    public MenuTurma(List<Turma> turmas) {
        this.turmas = turmas;
        if (!this.turmas.isEmpty()) {
            proximoId = this.turmas.stream().mapToInt(Turma::getId).max().orElse(0) + 1;
        } else {
            proximoId = 1;
        }
    }

    // --- MÉTODOS DE PONTO DE ENTRADA (CHAMADOS PELO MENU DO PROFESSOR) ---

    /**
     * Inicia o fluxo completo de gerenciamento de detalhes de uma turma,
     * começando pela seleção da turma desejada.
     * @param professor O professor logado que está realizando a operação.
     */
    public void iniciarGerenciamentoDetalhes(Professor professor) {
        System.out.println("\n--- Gerenciar Detalhes da Turma ---");
        Optional<Turma> turmaOpt = selecionarTurma(professor);

        turmaOpt.ifPresent(turma -> gerenciarDetalhes(turma, professor));
    }

    /**
     * Inicia o fluxo completo de gerenciamento de conteúdos de uma turma,
     * começando pela seleção da turma desejada.
     * @param professor O professor logado que está realizando a operação.
     */
    public void iniciarGerenciamentoConteudos(Professor professor) {
        System.out.println("\n--- Gerenciar Atividades e Tarefas ---");
        Optional<Turma> turmaOpt = selecionarTurma(professor);

        turmaOpt.ifPresent(turma -> gerenciarConteudos(turma, professor));
    }

    // --- MÉTODOS INTERNOS (MENUS ESPECÍFICOS E LÓGICA) ---

    /**
     * Isola a lógica de listar e selecionar uma turma pela qual um professor é responsável.
     * @param professor O professor cujas turmas serão listadas.
     * @return um Optional contendo a Turma selecionada pelo usuário, ou um Optional vazio caso
     * nenhuma turma seja selecionada ou encontrada.
     */
    private Optional<Turma> selecionarTurma(Professor professor) {
        List<Turma> minhasTurmas = this.turmas.stream()
                .filter(turma -> turma.getProfessoresResponsaveis().contains(professor))
                .collect(Collectors.toList());

        if (minhasTurmas.isEmpty()) {
            System.out.println("Você não é responsável por nenhuma turma.");
            return Optional.empty();
        }

        System.out.println("Suas Turmas:");
        minhasTurmas.forEach(turma -> System.out.println("ID: " + turma.getId() + " | Nome: " + turma.getNome()));
        System.out.print("\nDigite o ID da turma que deseja gerenciar (ou 0 para voltar): ");

        int idTurma;
        try {
            idTurma = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return Optional.empty();
        }

        if (idTurma == 0) return Optional.empty();

        Optional<Turma> turmaSelecionada = minhasTurmas.stream().filter(t -> t.getId() == idTurma).findFirst();
        if (turmaSelecionada.isEmpty()) {
            System.out.println("ID não encontrado ou não corresponde a uma de suas turmas.");
        }
        return turmaSelecionada;
    }

    /**
     * Exibe um menu para gerenciar detalhes da turma (nome, participantes).
     * @param turma A turma específica a ser gerenciada.
     * @param professorLogado O professor que está executando a ação.
     */
    public void gerenciarDetalhes(Turma turma, Professor professorLogado) {
        try {
            checarPermissao(turma, professorLogado);
        } catch (AcessoNaoAutorizadoException e) {
            System.out.println("ERRO DE PERMISSÃO: " + e.getMessage());
            return;
        }

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n====== GERENCIANDO DETALHES: " + turma.getNome() + " ======");
            System.out.println("1. Visualizar Lista de Participantes");
            System.out.println("2. Atualizar Informações da Turma");
            System.out.println("0. Voltar ao Menu Anterior");
            System.out.print("Escolha uma opção: ");

            int opcao;
            try { opcao = Integer.parseInt(scanner.nextLine()); }
            catch (NumberFormatException e) { System.out.println("Opção inválida."); continue; }

            switch (opcao) {
                case 1 -> turma.listarParticipantes();
                case 2 -> {
                    try {
                        System.out.println("\n--- Atualizando Informações ---");
                        atualizar(turma, professorLogado);
                    } catch (AcessoNaoAutorizadoException e) {
                        System.out.println("ERRO DE PERMISSÃO: " + e.getMessage());
                    }
                }
                case 0 -> continuar = false;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    /**
     * Exibe um menu para gerenciar conteúdos (atividades, tarefas).
     * @param turma A turma específica a ser gerenciada.
     * @param professorLogado O professor que está executando a ação.
     */
    public void gerenciarConteudos(Turma turma, Professor professorLogado) {
        try {
            checarPermissao(turma, professorLogado);
        } catch (AcessoNaoAutorizadoException e) {
            System.out.println("ERRO DE PERMISSÃO: " + e.getMessage());
            return;
        }

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n====== GERENCIANDO CONTEÚDOS: " + turma.getNome() + " ======");
            System.out.println("1. Listar Atividades e Tarefas");
            System.out.println("2. Adicionar Atividade");
            System.out.println("3. Remover Atividade");
            System.out.println("0. Voltar ao Menu Anterior");
            System.out.print("Escolha uma opção: ");

            int opcao;
            try { opcao = Integer.parseInt(scanner.nextLine()); }
            catch (NumberFormatException e) { System.out.println("Opção inválida."); continue; }

            switch (opcao) {
                case 1 -> {
                    System.out.println("\n--- Atividades da Turma ---");
                    turma.getAtividades().forEach(a -> System.out.println("- " + a.toString()));
                    System.out.println("\n--- Tarefas da Turma ---");
                    turma.getTarefas().forEach(t -> System.out.println("- " + t.toString()));
                }
                case 2 -> System.out.println("\n(Funcionalidade de adicionar atividade ainda não implementada.)");
                case 3 -> System.out.println("\n(Funcionalidade de remover atividade ainda não implementada.)");
                case 0 -> continuar = false;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    // --- MÉTODOS CRUD E DE APOIO ---

    /**
     * Verifica se o professor logado tem permissão para gerenciar a turma.
     * @param turma A turma a ser verificada.
     * @param professorLogado O professor cuja permissão será checada.
     * @throws AcessoNaoAutorizadoException se o professor não for um dos responsáveis pela turma.
     */
    private void checarPermissao(Turma turma, Professor professorLogado) throws AcessoNaoAutorizadoException {
        List<Professor> responsaveis = turma.getProfessoresResponsaveis();
        if (responsaveis.isEmpty() || !responsaveis.contains(professorLogado)) {
            throw new AcessoNaoAutorizadoException("Acesso negado. Você não é um professor responsável por esta turma.");
        }
    }

    /**
     * Atualiza o nome e a descrição de uma turma, se o professor tiver permissão.
     * @param t A turma a ser atualizada.
     * @param professorLogado O professor que está tentando realizar a ação.
     * @throws AcessoNaoAutorizadoException se o professor não tiver permissão.
     */
    public void atualizar(Turma t, Professor professorLogado) throws AcessoNaoAutorizadoException {
        checarPermissao(t, professorLogado);
        System.out.print("Novo nome (" + t.getNome() + "): ");
        String nome = scanner.nextLine();
        if (!nome.isBlank()) t.setNome(nome);
        System.out.print("Nova descrição (" + t.getDesc() + "): ");
        String desc = scanner.nextLine();
        if (!desc.isBlank()) t.setDesc(desc);
        System.out.println("Turma atualizada com sucesso!");
    }

    /**
     * Cria uma nova turma e define o professor logado como o primeiro responsável.
     * @param responsavel O Professor que está criando a turma.
     */
    public void criar(Professor responsavel) {
        System.out.println("\n--- Criar Nova " + getNomeEntidade() + " ---");
        System.out.print("Nome da Turma: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição: ");
        String desc = scanner.nextLine();
        System.out.print("Data de Início (dd/mm/aaaa): ");
        String inicio = scanner.nextLine();
        System.out.print("Data de Fim (dd/mm/aaaa): ");
        String fim = scanner.nextLine();
        Turma novaTurma = new Turma(proximoId++, nome, desc, inicio, fim, new Pessoa[]{responsavel}, null);
        turmas.add(novaTurma);
        System.out.println("Turma '" + nome + "' criada com sucesso (ID " + novaTurma.getId() + ") e você foi definido como responsável.");
    }

    @Override
    protected String getNomeEntidade() {
            return "Turma";
        }
}