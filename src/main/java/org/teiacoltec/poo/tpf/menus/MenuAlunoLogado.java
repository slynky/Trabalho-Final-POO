package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.menus.MainFrame;
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

        //Título
        JLabel titulo = new JLabel("Painel do Aluno - " + aluno.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#2C3E50"));
        add(titulo, BorderLayout.NORTH);

        //Painel de botões
        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnTurmas = criarBotao("Minhas Turmas");
        JButton btnAtividades = criarBotao("Atividades");
        JButton btnNotas = criarBotao("Minhas Notas");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnTurmas);
        botoes.add(btnAtividades);
        botoes.add(btnNotas);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        //Ações dos botões
        btnTurmas.addActionListener(e -> frame.trocarPainel("TURMAS_ALUNO"));
        btnAtividades.addActionListener(e -> frame.trocarPainel("ATIVIDADES_ALUNO"));
        btnNotas.addActionListener(e -> frame.trocarPainel("NOTAS_ALUNO"));
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.decode("#1976D2"));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Efeito de hover
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
