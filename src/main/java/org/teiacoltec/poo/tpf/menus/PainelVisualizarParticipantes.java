package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelVisualizarParticipantes extends JPanel {

    private MainFrame frame;
    private Professor professor;
    private JComboBox<Turma> comboTurmas;
    private JTable tabelaAlunos;
    private DefaultTableModel modeloTabela;

    public PainelVisualizarParticipantes(MainFrame frame, Professor professor) {
        this.frame = frame;
        this.professor = professor;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Topo: Seleção de Turma
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setOpaque(false);
        painelTopo.add(new JLabel("Selecione a Turma:"));
        comboTurmas = new JComboBox<>();
        comboTurmas.setPreferredSize(new Dimension(300, 30));
        painelTopo.add(comboTurmas);
        add(painelTopo, BorderLayout.NORTH);

        // Centro: Tabela
        String[] colunas = {"Matrícula", "Nome", "Email", "Curso"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaAlunos = new JTable(modeloTabela);
        tabelaAlunos.setRowHeight(25);
        add(new JScrollPane(tabelaAlunos), BorderLayout.CENTER);

        // Baixo: Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> frame.trocarPainel("PROFESSOR"));
        add(btnVoltar, BorderLayout.SOUTH);

        // Evento: Quando mudar a turma no combo, atualiza a tabela
        comboTurmas.addActionListener(e -> atualizarTabela());

        carregarTurmas();
    }

    private void carregarTurmas() {
        try {
            List<Turma> turmas = TurmaDAO.listarTurmasPorProfessor(professor.getCpf());
            for (Turma t : turmas) comboTurmas.addItem(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        Turma turmaSel = (Turma) comboTurmas.getSelectedItem();
        if (turmaSel == null) return;

        try {
            List<Aluno> alunos = AlunoDAO.listarTodosAlunosDaTurma(turmaSel.getId());
            for (Aluno a : alunos) {
                modeloTabela.addRow(new Object[]{
                        a.getMatricula(), a.getNome(), a.getEmail(), a.getCurso()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao listar alunos: " + e.getMessage());
        }
    }
}