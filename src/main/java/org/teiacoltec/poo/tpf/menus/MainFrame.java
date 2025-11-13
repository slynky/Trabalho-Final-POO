package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.pessoa.*;
import org.teiacoltec.poo.tpf.util.Autenticacao;
import org.teiacoltec.poo.tpf.menus.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Pessoa usuarioLogado;
    private List<Pessoa> usuarios;

    // Painéis dinâmicos
    private LoginPanel loginPanel;
    private MenuProfessorLogado painelProfessor;
    private MenuAlunoLogado painelAluno;
    private MenuMonitorLogado painelMonitor;

    public MainFrame(List<Pessoa> usuarios) {
        this.usuarios = usuarios;

        setTitle("Sistema Acadêmico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this, usuarios);

        // Inicializa painéis vazios (serão criados após login)
        painelProfessor = null;
        painelAluno = null;
        painelMonitor = null;

        // Adiciona login inicial
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel);
        setVisible(true);
    }

    public void autenticarUsuario(Pessoa usuario) {
        this.usuarioLogado = usuario;

        if (usuario instanceof Professor professor) {
            painelProfessor = new MenuProfessorLogado(this, professor);
            mainPanel.add(painelProfessor, "PROFESSOR");
            trocarPainel("PROFESSOR");

        } else if (usuario instanceof Aluno aluno) {
            painelAluno = new MenuAlunoLogado(this, aluno);
            mainPanel.add(painelAluno, "ALUNO");
            trocarPainel("ALUNO");

        } else if (usuario instanceof Monitor monitor) {
            painelMonitor = new MenuMonitorLogado(this, monitor);
            mainPanel.add(painelMonitor, "MONITOR");
            trocarPainel("MONITOR");
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de usuário desconhecido!", "Erro", JOptionPane.ERROR_MESSAGE);
            trocarPainel("LOGIN");
        }
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

    public List<Pessoa> getUsuarios() {
        return usuarios;
    }
}
