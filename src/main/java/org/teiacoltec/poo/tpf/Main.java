package org.teiacoltec.poo.tpf;


import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.exceptions.CredenciaisInvalidasException;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.menus.MenuAlunoLogado;
import org.teiacoltec.poo.tpf.menus.MenuMonitorLogado;
import org.teiacoltec.poo.tpf.menus.MenuProfessorLogado;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;
import org.teiacoltec.poo.tpf.util.Autenticacao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static List<Atividade> atividades = new  ArrayList<>();
    private static List<Turma> turmas = new ArrayList<>();

    public static Atividade retornarAtividade(int idAtividade)  {
        try {


           for(Atividade atividade : atividades) {
               if(atividade.getId() == idAtividade) { return atividade; }
           }
        }catch (Exception e) {
            System.err.println("Nenhuma atividade com esse ID foi encontrada");
        }

        return null;
    }



    public static void main(String[] args) throws SQLException {

        Atividade neiss = new Atividade(1, "Dever de matematica", "Fazer a pagina indicada", "12/07/2024", "13/07/2024", 21);
        AtividadeDAO.insetirAtividade(neiss);
        Atividade teste = AtividadeDAO.buscarPorId(1).get();
        System.out.println(teste.getNome());
        Turma turmaBoa = new Turma(1, "Turma boa", "Uma turma deveras boa", "01/01/2001", "01/01/2002", null, null);
        TurmaDAO.inserirTurma(turmaBoa);
        Tarefa tarefaTeste = new Tarefa(1, "Fazer uns negocio ai", turmaBoa, neiss, 12.4F);
        TarefaDAO.inserirTarefa(tarefaTeste);

        Scanner scanner = new Scanner(System.in);
        boolean executarSistema = false;
        while (executarSistema) {
            // A cópia dos dados é feita a cada ciclo de login para garantir que as alterações
            // de uma sessão possam ser salvas ou descartadas.
            List<Turma> turmasCopia = turmas.stream()
                    .map(Turma::new) // Utiliza o construtor de cópia de Turma
                    .collect(Collectors.toList());

            // Coleta todos os usuários de todas as turmas para a autenticação
            List<Pessoa> todosOsUsuarios = obterTodosOsUsuarios(turmasCopia);
            Pessoa usuarioLogado = null;

            // --- ETAPA DE LOGIN ---
            try {
                System.out.println("\n====== SISTEMA DE GERENCIAMENTO ESCOLAR ======");
                System.out.println("Por favor, faça o login para continuar.");
                System.out.print("Login (CPF): ");
                String login = scanner.nextLine();
                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                usuarioLogado = Autenticacao.autenticar(login, senha, todosOsUsuarios);
                System.out.println("\n>>> Bem-vindo(a), " + usuarioLogado.getNome() + "!");

            } catch (CredenciaisInvalidasException e) {
                System.err.println("### ERRO: " + e.getMessage());
                System.out.print("Deseja tentar novamente (s/n)? ");
                if (!scanner.nextLine().equalsIgnoreCase("s")) {
                    executarSistema = false;
                }
                continue; // Volta para o início do loop while
            }

            // --- DIRECIONAMENTO PARA MENU ESPECIALIZADO ---
            if (usuarioLogado instanceof Professor professor) {
                // Inicia o menu do professor, que retorna a lista de turmas modificada
                turmasCopia = MenuProfessorLogado.exibirMenu(professor, turmasCopia, scanner);
            } else if (usuarioLogado instanceof Aluno aluno) {
                // Inicia o menu do aluno
                MenuAlunoLogado.exibirMenu(aluno, turmasCopia, scanner);
            } else if (usuarioLogado instanceof Monitor monitor) {
                // Inicia menu para o monitor
                MenuMonitorLogado.exibirMenu(monitor, turmasCopia, scanner);
            }

            // --- ETAPA DE SALVAMENTO ---
            System.out.print("Você realizou alterações. Deseja salvar antes de sair (s/n)? ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                turmas = turmasCopia; // Atualiza a lista principal com a cópia modificada

            } else {
                System.out.println(">>> Alterações descartadas.");
            }

            System.out.print("Deseja realizar um novo login ou encerrar o programa (login/encerrar)? ");
            if (scanner.nextLine().equalsIgnoreCase("encerrar")) {
                executarSistema = false;
            }
        }
        System.out.println(">>> Programa encerrado.");


    }

    /**
     * Extrai todos os participantes (Alunos, Professores, Monitores) de todas as turmas
     * e os agrupa em uma única lista de Pessoas.
     * @param turmas A lista de turmas do sistema.
     * @return Uma lista contendo todos os usuários cadastrados.
     */
    private static List<Pessoa> obterTodosOsUsuarios(List<Turma> turmas) {
        return turmas.stream()
                .flatMap(turma -> turma.getParticipantes().stream())
                .collect(Collectors.toList());
    }


    /**
     * Popula com dados iniciais
     */
    private static void popularDadosIniciais() {
        turmas.clear();


        Professor p1 = new Professor("123.456.789-00", "Carlos Pereira", "10/05/1985", "carlos.p@escola.edu", "Rua das Flores, 123", "P001", "Doutorado", "1");
        Aluno a1 = new Aluno("111.222.333-44", "Beatriz Costa", "20/03/2005", "bia.costa@email.com", "Av. Principal, 456", "A001", "Ciência da Computação", "1");
        Monitor m1 = new Monitor("444.555.666-77", "Lucas Martins", "28/05/1999", "lucas.m@email.com", "Travessa dos Sonhos, 78", "M001", "Ciência da Computação", "1");

        Turma turmaPrincipal = new Turma(1, "Desenvolvimento de Software 2025", "POO e Java", "01/08/2025", "15/12/2025", null, null);
        turmaPrincipal.adicionarParticipante(p1);
        turmaPrincipal.adicionarParticipante(m1);

        turmas.add(turmaPrincipal);
    }
}