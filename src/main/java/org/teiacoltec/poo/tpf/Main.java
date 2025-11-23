package org.teiacoltec.poo.tpf;

import org.teiacoltec.poo.tpf.conexao.dao.*;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.menus.MainFrame;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static List<Turma> turmas = new ArrayList<>();

    public static void main(String[] args) throws SQLException {

        popularDadosIniciais();

        List<Pessoa> todosUsuarios = obterTodosOsUsuarios(turmas);

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(todosUsuarios);
            mainFrame.setVisible(true);
        });

        testeDAO();
    }

    private static void popularDadosIniciais() {
        turmas.clear();

        System.out.println("--- Populando Dados Iniciais ---");

        Professor p1 = new Professor("12345678900", "Carlos Pereira", "10/05/1985", "carlos.p@escola.edu", "Rua das Flores, 123", "P001", "Doutorado", "1");
        Aluno a1 = new Aluno("11122233344", "Beatriz Costa", "20/03/2005", "bia.costa@email.com", "Av. Principal, 456", "A001", "Ciência da Computação", "1");
        Monitor m1 = new Monitor("44455566677", "Lucas Martins", "28/05/1999", "lucas.m@email.com", "Travessa dos Sonhos, 78", "M001", "Ciência da Computação", "1");

        inserirPessoaSeguro(p1);
        inserirPessoaSeguro(a1);
        inserirPessoaSeguro(m1);

        Turma turmaPrincipal = new Turma(0, "Desenvolvimento de Software 2025", "POO e Java", "01/08/2025", "15/12/2025", new ArrayList<>(), null);

        turmaPrincipal.adicionarParticipante(p1);
        turmaPrincipal.adicionarParticipante(a1);
        turmaPrincipal.adicionarParticipante(m1);
        turmas.add(turmaPrincipal);

        try {
            if (TurmaDAO.obterTurmaPorId(1).isPresent()) {
                System.out.println("Turma ID 1 já existe. Usando ela.");
                turmaPrincipal.setId(1);
            } else {
                TurmaDAO.inserirTurma(turmaPrincipal);
                System.out.println("Nova turma criada com ID: " + turmaPrincipal.getId());
            }

            int idCorreto = turmaPrincipal.getId();

            vincularParticipante(p1.getCpf(), idCorreto);
            vincularParticipante(a1.getCpf(), idCorreto);

            vincularParticipante(m1.getCpf(), idCorreto);

        } catch (SQLException e) {
            System.err.println("Erro ao sincronizar turma com banco: " + e.getMessage());
        }
    }

    private static List<Pessoa> obterTodosOsUsuarios(List<Turma> turmas) {
        return turmas.stream()
                .flatMap(turma -> turma.getParticipantes().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private static void inserirPessoaSeguro(Pessoa p) {
        try {
            PessoaDAO.inserir(p);
        } catch (SQLException e) {
            if (e.getErrorCode() != 1062) e.printStackTrace(); // Ignora erro de chave duplicada
        }
    }

    private static void vincularParticipante(String cpf, int idTurma) {
        try {
            TurmaDAO.inserirParticipante(cpf, idTurma);
            System.out.println("Vinculado com sucesso: " + cpf + " -> Turma " + idTurma);
        } catch (SQLException e) {
        }
    }

    private static void testeDAO() throws SQLException {

        System.out.println("--- INICIANDO TESTES DAO ---");

        // 1. DADOS DE ENTRADA
        Professor p1 = new Professor("12345678900", "Carlos Pereira", "10/05/1985", "carlos.p@escola.edu", "Rua das Flores, 123", "P001", "Doutorado", "senha123");
        Turma turmaOriginal = new Turma(1, "Desenvolvimento de Software 2025", "POO e Java", "01/08/2025", "15/12/2025", new ArrayList<>(), null);
        turmaOriginal.adicionarParticipante(p1);

        Atividade atividadeOriginal = new Atividade(100, "Atividade Antiga", "Descrição Original", "05/01/2026", "15/01/2026", 50.0f);
        Tarefa tarefaOriginal = new Tarefa(200, "Tarefa 1.0 - Inicial", turmaOriginal, atividadeOriginal, 10.0f);


        // --- TESTE 1: INSERÇÃO ---
        System.out.println("\n== 1. TESTE DE INSERÇÃO ==");

        try {
            TurmaDAO.inserirTurma(turmaOriginal);
            System.out.println("Turma inserida.");
        } catch (SQLException e) {
            System.out.println("Aviso: Turma já existe ou erro: " + e.getMessage());
        }

        // Tenta inserir Atividade
        try {
            AtividadeDAO.inserirAtividade(atividadeOriginal);
            System.out.println("Atividade inserida.");
        } catch (SQLException e) {
            System.out.println("Aviso: Atividade já existe ou erro: " + e.getMessage());
        }

        // Tenta inserir Tarefa
        try {
            TarefaDAO.inserirTarefa(tarefaOriginal);
            System.out.println("Tarefa inserida.");
        } catch (SQLException e) {
            System.out.println("Aviso: Tarefa já existe ou erro: " + e.getMessage());
        }


        // --- TESTE 2: ATUALIZAÇÃO DA ATIVIDADE ---

        System.out.println("\n== 2. ATUALIZAÇÃO DA ATIVIDADE ==");

        atividadeOriginal.setNome("Projeto Final do Módulo");
        atividadeOriginal.setValor(100.0f);
        atividadeOriginal.setDesc("Nova descrição: Projeto completo.");
        atividadeOriginal.setFim(LocalDate.now().plusDays(30).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));


        AtividadeDAO.atualizarAtividade(atividadeOriginal);

        Atividade atividadeAtualizada = AtividadeDAO.buscarPorId(100).orElseThrow(() -> new SQLException("Atividade não encontrada."));
        System.out.println("Nome antigo: Atividade Antiga");
        System.out.println("Nome novo: " + atividadeAtualizada.getNome());
        System.out.println("Valor novo: " + atividadeAtualizada.getValor());
        System.out.println("Atividade atualizada com sucesso.");

        // --- TESTE 3: ATUALIZAÇÃO DA PESSOA (UPSET via TurmaDAO) ---

        System.out.println("\n== 3. ATUALIZAÇÃO DA PESSOA (via Upsert) ==");

        p1.setNome("Dr. Carlos Pereira (Coordenador)");
        turmaOriginal.setNome("Desenvolvimento Avançado");

        TurmaDAO.atualizarTurma(turmaOriginal);

        Turma turmaAtualizada = TurmaDAO.obterTurmaPorId(1).orElseThrow(() -> new SQLException("Turma não encontrada."));
        Pessoa pessoaAtualizada = turmaAtualizada.getParticipantes().stream()
                .filter(p -> p.getCpf().equals("12345678900"))
                .findFirst()
                .orElseThrow(() -> new SQLException("ERRO CRÍTICO: Professor (12345678900) não foi encontrado na turma após a atualização."));

        System.out.println("Nome da Turma novo: " + turmaAtualizada.getNome());
        System.out.println("Nome do Professor atualizado: " + pessoaAtualizada.getNome());
        System.out.println("Pessoa e Turma atualizadas com sucesso.");


        // --- TESTE 4: ATUALIZAÇÃO DA TAREFA ---

        System.out.println("\n== 4. ATUALIZAÇÃO DA TAREFA ==");

        Tarefa tarefaAtualizar = TarefaDAO.buscarPorId(200).orElseThrow(() -> new SQLException("Tarefa não encontrada."));

        tarefaAtualizar.setNome("Tarefa 2.0 - Final");
        tarefaAtualizar.setNota(25.0f);

        TarefaDAO.atualizarTarefa(tarefaAtualizar);

        Tarefa tarefaFinal = TarefaDAO.buscarPorId(200).get();
        System.out.println("Nome da Tarefa novo: " + tarefaFinal.getNome());
        System.out.println("Nota máxima nova: " + tarefaFinal.getNota());
        System.out.println("Tarefa atualizada com sucesso.");

        System.out.println("\n--- TESTES DAO CONCLUÍDOS COM SUCESSO ---");
    }
}