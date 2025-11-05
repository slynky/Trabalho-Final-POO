package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;

import javax.swing.*;
import java.awt.*;

public class MenuMonitorLogado extends JPanel {

    private Monitor monitor;

    public MenuMonitorLogado(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Painel do Monitor", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.decode("#2C3E50"));
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 10, 10));
        botoes.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JButton btnVisualizarAlunos = new JButton("Visualizar Alunos");
        JButton btnLancarNotas = new JButton("Lançar Notas");
        JButton btnConsultarNotas = new JButton("Consultar Notas");
        JButton btnSair = new JButton("Sair");

        estilizarBotao(btnVisualizarAlunos);
        estilizarBotao(btnLancarNotas);
        estilizarBotao(btnConsultarNotas);
        estilizarBotao(btnSair);

        // ações
        btnVisualizarAlunos.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "👥 Lista de alunos da turma monitorada (integre com TurmaDAO).",
                "Alunos da Turma", JOptionPane.INFORMATION_MESSAGE));

        btnLancarNotas.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "✏️ Você pode lançar novas notas (sem edição posterior).",
                "Lançar Notas", JOptionPane.INFORMATION_MESSAGE));

        btnConsultarNotas.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "📊 Consulte as notas já lançadas.",
                "Consultar Notas", JOptionPane.INFORMATION_MESSAGE));

        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));

        botoes.add(btnVisualizarAlunos);
        botoes.add(btnLancarNotas);
        botoes.add(btnConsultarNotas);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);
    }

    private void estilizarBotao(JButton botao) {
        botao.setBackground(Color.decode("#388E3C"));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 16));
    }
}

