package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelMonitorAlunos extends JPanel {

    public PainelMonitorAlunos(MainFrame frame, Monitor monitor, Turma turma) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel("Alunos da Turma: " + (turma != null ? turma.getNome() : "N/A"), SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lbl, BorderLayout.NORTH);

        String[] colunas = {"Matrícula", "Nome", "Email"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> frame.trocarPainel("MONITOR"));
        add(btnVoltar, BorderLayout.SOUTH);

        if (turma != null) {
            try {
                List<Aluno> alunos = AlunoDAO.listarTodosAlunosDaTurma(turma.getId());
                for (Aluno a : alunos) {
                    modelo.addRow(new Object[]{a.getMatricula(), a.getNome(), a.getEmail()});
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar alunos: " + e.getMessage());
            }
        }
    }
}