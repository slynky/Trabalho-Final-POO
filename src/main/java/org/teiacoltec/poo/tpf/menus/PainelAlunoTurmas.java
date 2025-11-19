package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelAlunoTurmas extends JPanel {

    private MainFrame frame;
    private Aluno aluno;
    private JTable tabela;
    private DefaultTableModel modelo;

    public PainelAlunoTurmas(MainFrame frame, Aluno aluno) {
        this.frame = frame;
        this.aluno = aluno;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lbl = new JLabel("Minhas Turmas Matriculadas", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.decode("#2C3E50"));
        add(lbl, BorderLayout.NORTH);

        // Tabela
        String[] colunas = {"Nome da Turma", "Início", "Fim", "Descrição"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.addActionListener(e -> frame.trocarPainel("ALUNO"));
        add(btnVoltar, BorderLayout.SOUTH);

        carregarTurmas();
    }

    private void carregarTurmas() {
        try {
            List<Turma> turmas = TurmaDAO.listarTurmasPorAluno(aluno.getCpf());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Turma t : turmas) {
                modelo.addRow(new Object[]{
                        t.getNome(),
                        t.getInicio().format(String.valueOf(fmt)),
                        t.getFim().format(String.valueOf(fmt)),
                        t.getDesc()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar turmas: " + e.getMessage());
        }
    }
}