package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelLancarNotas extends JPanel {

    private MainFrame frame;
    private Professor professor;
    private JComboBox<Turma> comboTurmas;
    private JComboBox<Tarefa> comboTarefas;
    private JTable tabelaNotas;
    private DefaultTableModel modeloTabela;

    public PainelLancarNotas(MainFrame frame, Professor professor) {
        this.frame = frame;
        this.professor = professor;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Filtros (Topo) ---
        JPanel painelFiltros = new JPanel(new GridLayout(2, 2, 10, 10));
        painelFiltros.add(new JLabel("1. Selecione a Turma:"));
        comboTurmas = new JComboBox<>();
        painelFiltros.add(comboTurmas);

        painelFiltros.add(new JLabel("2. Selecione a Tarefa:"));
        comboTarefas = new JComboBox<>();
        painelFiltros.add(comboTarefas);
        add(painelFiltros, BorderLayout.NORTH);

        // --- Tabela (Centro) ---
        String[] colunas = {"CPF Aluno", "Nome", "Nota Atual"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaNotas = new JTable(modeloTabela);
        add(new JScrollPane(tabelaNotas), BorderLayout.CENTER);

        // --- Botões (Baixo) ---
        JPanel botoes = new JPanel();
        JButton btnLancar = new JButton("Lançar Nota para Selecionado");
        JButton btnVoltar = new JButton("Voltar");
        botoes.add(btnLancar);
        botoes.add(btnVoltar);
        add(botoes, BorderLayout.SOUTH);

        // --- Eventos ---
        btnVoltar.addActionListener(e -> frame.trocarPainel("PROFESSOR"));

        // Quando seleciona turma, carrega tarefas
        comboTurmas.addActionListener(e -> carregarTarefas());

        // Quando seleciona tarefa, carrega alunos
        comboTarefas.addActionListener(e -> carregarAlunos());

        btnLancar.addActionListener(e -> lancarNotaDialogo());

        carregarTurmas();
    }

    // ... Métodos de carregarTurmas (igual aos outros painéis) ...
    private void carregarTurmas() {
        try {
            comboTurmas.removeAllItems();
            List<Turma> turmas = TurmaDAO.listarTurmasPorProfessor(professor.getCpf());
            for (Turma t : turmas) comboTurmas.addItem(t);
        } catch (Exception e) {}
    }

    private void carregarTarefas() {
        try {
            comboTarefas.removeAllItems();
            Turma t = (Turma) comboTurmas.getSelectedItem();
            if (t != null) {
                List<Tarefa> tarefas = TarefaDAO.listarTarefasPorTurma(t.getId());
                // Pequeno truque para JComboBox de Tarefa funcionar bonito: sobrescrever toString na classe Tarefa também!
                for (Tarefa task : tarefas) comboTarefas.addItem(task);
            }
        } catch (Exception e) {}
    }

    private void carregarAlunos() {
        modeloTabela.setRowCount(0);
        Turma t = (Turma) comboTurmas.getSelectedItem();
        if (t == null) return;

        try {
            List<Aluno> alunos = AlunoDAO.listarTodosAlunosDaTurma(t.getId());
            for (Aluno a : alunos) {
                modeloTabela.addRow(new Object[]{a.getCpf(), a.getNome(), " - "});
            }
        } catch (Exception e) {}
    }

    private void lancarNotaDialogo() {
        int row = tabelaNotas.getSelectedRow();
        Tarefa tarefa = (Tarefa) comboTarefas.getSelectedItem();

        if (row == -1 || tarefa == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa e um aluno na tabela.");
            return;
        }

        String cpfAluno = (String) tabelaNotas.getValueAt(row, 0);
        String nomeAluno = (String) tabelaNotas.getValueAt(row, 1);

        String notaStr = JOptionPane.showInputDialog(this, "Digite a nota para " + nomeAluno + ":");
        if (notaStr != null) {
            try {
                float nota = Float.parseFloat(notaStr);
                TarefaDAO.lancarNota(tarefa.getId(), cpfAluno, nota);
                JOptionPane.showMessageDialog(this, "Nota lançada!");
                // Idealmente, recarregar tabela para mostrar a nota
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }
}