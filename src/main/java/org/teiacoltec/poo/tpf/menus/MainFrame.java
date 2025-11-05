package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.pessoa.*;
import org.teiacoltec.poo.tpf.util.Autenticacao;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Pessoa usuarioLogado;
    private List<Pessoa> usuarios;

    public MainFrame(List<Pessoa> usuarios) {
        this.usuarios = usuarios;
        setTitle("Sistema Acadêmico - Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Adiciona painéis dinâmicos
        mainPanel.add(new LoginPanel(this, usuarios), "LOGIN");
        mainPanel.add(new MenuProfessorLogado(this), "PROFESSOR");
        mainPanel.add(new MenuAlunoLogado(this), "ALUNO");
        mainPanel.add(new MenuMonitorLogado(this), "MONITOR");

        add(mainPanel);
        setVisible(true);
    }

    public void trocarPainel(String nomePainel) {
        cardLayout.show(mainPanel, nomePainel);
    }

    public void setUsuarioLogado(Pessoa usuario) {
        this.usuarioLogado = usuario;
    }

    public Pessoa getUsuarioLogado() {
        return usuarioLogado;
    }
}
