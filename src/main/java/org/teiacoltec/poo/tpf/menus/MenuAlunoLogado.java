package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import javax.swing.*;
import java.awt.*;

public class MenuAlunoLogado extends JPanel {

    private final Aluno aluno;

    public MenuAlunoLogado(MainFrame frame, Aluno aluno) {
        this.aluno = aluno;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("Painel do Aluno - " + aluno.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#2C3E50"));
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(3, 1, 15, 15)); // Reduzi para 3 linhas
        botoes.setOpaque(false);

        JButton btnTurmas = criarBotao("Minhas Turmas");
        JButton btnAtividades = criarBotao("Consultar Atividades");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnTurmas);
        botoes.add(btnAtividades);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        btnTurmas.addActionListener(e -> frame.trocarPainel("ALUNO_TURMAS"));
        btnAtividades.addActionListener(e -> frame.trocarPainel("ALUNO_ATIVIDADES"));
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.decode("#1976D2"));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.decode("#1565C0"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.decode("#1976D2"));
            }
        });
        return botao;
    }
}