package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;

import javax.swing.*;
import java.awt.*;

public class MenuProfessorLogado extends JPanel {

    public MenuProfessorLogado(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Painel do Professor", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 10, 10));
        botoes.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JButton btnTurmas = new JButton("Gerenciar Turmas");
        JButton btnAtividades = new JButton("Associar Atividades");
        JButton btnNotas = new JButton("Lançar Notas");
        JButton btnParticipantes = new JButton("Visualizar Alunos");
        JButton btnSair = new JButton("Sair");

        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));

        botoes.add(btnTurmas);
        botoes.add(btnAtividades);
        botoes.add(btnNotas);
        botoes.add(btnParticipantes);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);
    }
}

