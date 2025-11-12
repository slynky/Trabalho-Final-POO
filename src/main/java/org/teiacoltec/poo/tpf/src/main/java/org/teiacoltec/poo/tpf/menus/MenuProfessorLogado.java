package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.menus.MainFrame;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;

import javax.swing.*;
import java.awt.*;

public class MenuProfessorLogado extends JPanel {

    private final Professor professor;

    public MenuProfessorLogado(MainFrame frame, Professor professor) {
        this.professor = professor;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Título
        JLabel titulo = new JLabel("Painel do Professor - " + professor.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#0D47A1"));
        add(titulo, BorderLayout.NORTH);

        // Painel de botões
        JPanel botoes = new JPanel(new GridLayout(5, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnTurmas = criarBotao("Gerenciar Turmas");
        JButton btnAtividades = criarBotao("Associar Atividades");
        JButton btnNotas = criarBotao("Lançar Notas");
        JButton btnParticipantes = criarBotao("Visualizar Alunos");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnTurmas);
        botoes.add(btnAtividades);
        botoes.add(btnNotas);
        botoes.add(btnParticipantes);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        //Ações dos botões
        btnTurmas.addActionListener(e -> frame.trocarPainel("GERENCIAR_TURMAS"));
        btnAtividades.addActionListener(e -> frame.trocarPainel("ASSOCIAR_ATIVIDADES"));
        btnNotas.addActionListener(e -> frame.trocarPainel("LANCAR_NOTAS_PROF"));
        btnParticipantes.addActionListener(e -> frame.trocarPainel("VISUALIZAR_PARTICIPANTES"));
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.decode("#1565C0"));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.decode("#0D47A1"));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.decode("#1565C0"));
            }
        });
        return botao;
    }
}
