package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;

import javax.swing.*;
import java.awt.*;

public class MenuAlunoLogado extends JPanel {

    private Aluno aluno;

    public MenuAlunoLogado(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Painel do Aluno", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.decode("#2C3E50"));
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 10, 10));
        botoes.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JButton btnTurmas = new JButton("Minhas Turmas");
        JButton btnAtividades = new JButton("Atividades");
        JButton btnNotas = new JButton("Minhas Notas");
        JButton btnSair = new JButton("Sair");

        estilizarBotao(btnTurmas);
        estilizarBotao(btnAtividades);
        estilizarBotao(btnNotas);
        estilizarBotao(btnSair);

        // ações
        btnTurmas.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Suas turmas serão listadas aqui (integre com TurmaDAO).",
                "Turmas do Aluno", JOptionPane.INFORMATION_MESSAGE));

        btnAtividades.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "📝 Suas atividades serão exibidas aqui (integre com AtividadeDAO).",
                "Atividades", JOptionPane.INFORMATION_MESSAGE));

        btnNotas.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "📊 Suas notas serão exibidas aqui (integre com NotaDAO).",
                "Notas", JOptionPane.INFORMATION_MESSAGE));

        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));

        botoes.add(btnTurmas);
        botoes.add(btnAtividades);
        botoes.add(btnNotas);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);
    }

    private void estilizarBotao(JButton botao) {
        botao.setBackground(Color.decode("#1976D2"));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 16));
    }
}

