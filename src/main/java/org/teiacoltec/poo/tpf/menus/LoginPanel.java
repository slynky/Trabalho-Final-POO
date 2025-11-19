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
    private JButton btnEntrar, btnCadastrar;
    private JLabel lblErro;

    public LoginPanel(MainFrame frame, List<Pessoa> usuarios) {
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#F7F9FB"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Login do Sistema Acadêmico", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#2C3E50"));
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
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

        lblErro = new JLabel(" ");
        lblErro.setForeground(Color.RED);
        lblErro.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(lblErro, gbc);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 15, 0));
        painelBotoes.setBackground(Color.decode("#F7F9FB"));

        btnEntrar = new JButton("Entrar");
        estilizarBotao(btnEntrar, "#1976D2");
        btnEntrar.addActionListener(e -> autenticar(frame, usuarios));

        painelBotoes.add(btnEntrar);

        gbc.gridy = 4;
        add(painelBotoes, gbc);
    }

    /**
     * Tenta autenticar o usuário e direciona para o menu correto.
     */
    private void autenticar(MainFrame frame, List<Pessoa> usuarios) {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            lblErro.setText("Preencha todos os campos!");
            return;
        }

        try {
            Pessoa usuario = Autenticacao.autenticar(login, senha, usuarios);
            lblErro.setText(" ");
            frame.autenticarUsuario(usuario);
        } catch (CredenciaisInvalidasException ex) {
            lblErro.setText("CPF ou senha inválidos!");
        }
    }

    private void estilizarBotao(JButton botao, String corHex) {
        botao.setBackground(Color.decode(corHex));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 16));
    }
}
