package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.exceptions.CredenciaisInvalidasException;
import org.teiacoltec.poo.tpf.pessoa.*;
import org.teiacoltec.poo.tpf.util.Autenticacao;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginPanel extends JPanel {

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JButton btnEntrar;
    private JLabel lblErro;

    public LoginPanel(MainFrame frame, List<Pessoa> usuarios) {
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#F7F9FB"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Login do Sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#333333"));
        gbc.gridwidth = 2;
        add(titulo, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("CPF:"), gbc);
        campoLogin = new JTextField(15);
        gbc.gridx = 1;
        add(campoLogin, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Senha:"), gbc);
        campoSenha = new JPasswordField(15);
        gbc.gridx = 1;
        add(campoSenha, gbc);

        lblErro = new JLabel("");
        lblErro.setForeground(Color.RED);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(lblErro, gbc);

        btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(Color.decode("#1976D2"));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.addActionListener(e -> autenticar(frame, usuarios));
        gbc.gridy = 4;
        add(btnEntrar, gbc);
    }

    private void autenticar(MainFrame frame, List<Pessoa> usuarios) {
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());

        try {
            Pessoa usuario = Autenticacao.autenticar(login, senha, usuarios);
            frame.setUsuarioLogado(usuario);

            if (usuario instanceof Professor)
                frame.trocarPainel("PROFESSOR");
            else if (usuario instanceof Aluno)
                frame.trocarPainel("ALUNO");
            else if (usuario instanceof Monitor)
                frame.trocarPainel("MONITOR");

        } catch (CredenciaisInvalidasException ex) {
            lblErro.setText("CPF ou senha inválidos!");
        }
    }
}

