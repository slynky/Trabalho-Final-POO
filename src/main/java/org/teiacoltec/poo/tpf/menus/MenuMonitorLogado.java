package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.menus.MainFrame;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;

import javax.swing.*;
import java.awt.*;

public class MenuMonitorLogado extends JPanel {

    private final Monitor monitor;

    public MenuMonitorLogado(MainFrame frame, Monitor monitor) {
        this.monitor = monitor;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        //Título
        JLabel titulo = new JLabel("Painel do Monitor - " + monitor.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#1B5E20"));
        add(titulo, BorderLayout.NORTH);

        //Painel de botões
        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnVisualizarAlunos = criarBotao("Visualizar Alunos");
        JButton btnLancarNotas = criarBotao("Lançar Notas");
        JButton btnConsultarNotas = criarBotao("Consultar Notas");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnVisualizarAlunos);
        botoes.add(btnLancarNotas);
        botoes.add(btnConsultarNotas);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        // Ações dos botões
        btnVisualizarAlunos.addActionListener(e -> frame.trocarPainel("ALUNOS_MONITOR"));
        btnLancarNotas.addActionListener(e -> frame.trocarPainel("LANCAR_NOTAS"));
        btnConsultarNotas.addActionListener(e -> frame.trocarPainel("CONSULTAR_NOTAS"));
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.decode("#43A047"));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //  hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.decode("#2E7D32"));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.decode("#43A047"));
            }
        });
        return botao;
    }
}
