package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelMonitorTarefas extends JPanel {

    public PainelMonitorTarefas(MainFrame frame, Monitor monitor, Turma turma) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel("Tarefas Cadastradas", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lbl, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome da Tarefa", "Atividade Base", "Valor"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> frame.trocarPainel("MONITOR"));
        add(btnVoltar, BorderLayout.SOUTH);

        if (turma != null) {
            try {
                List<Tarefa> tarefas = TarefaDAO.listarTarefasPorTurma(turma.getId());
                for (Tarefa t : tarefas) {
                    modelo.addRow(new Object[]{
                            t.getId(), t.getNome(), t.getNome(), t.getNota()
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar tarefas: " + e.getMessage());
            }
        }
    }
}