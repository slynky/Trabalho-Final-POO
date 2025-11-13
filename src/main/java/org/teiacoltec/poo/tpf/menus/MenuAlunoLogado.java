package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnTurmas = criarBotao("Visualizar Turmas");
        JButton btnAtividades = criarBotao("Consultar Atividades");
        JButton btnBuscarAtividade = criarBotao("Buscar Atividade por ID");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnTurmas);
        botoes.add(btnAtividades);
        botoes.add(btnBuscarAtividade);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        btnTurmas.addActionListener(e -> listarTurmasAluno());
        btnAtividades.addActionListener(e -> listarTodasAtividades());
        btnBuscarAtividade.addActionListener(e -> buscarAtividadePorId());
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    private void listarTurmasAluno() {
        try {
            List<String> nomesTurmas = TurmaDAO.listarNomesTurmaPorCpf(aluno.getCpf());

            if (nomesTurmas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Você ainda não está matriculado em nenhuma turma.",
                        "Minhas Turmas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("Suas Turmas:\n\n");
            for (String nome : nomesTurmas) {
                sb.append("• ").append(nome).append("\n");
            }

            // Usamos JTextArea dentro de JScrollPane para formatar melhor a lista
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(350, 200));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Turmas do Aluno", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar turmas: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void listarTodasAtividades() {
        try {

            List<Atividade> atividades = AtividadeDAO.listarTodas();

            if (atividades.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma atividade cadastrada no sistema.",
                        "Atividades", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("Todas as Atividades:\n\n");
            for (Atividade a : atividades) {
                sb.append("- ").append(a.getNome()).append(" (ID: ").append(a.getId())
                        .append(", Valor: ").append(a.getValor()).append(")\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Atividades", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar atividades: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarAtividadePorId() {
        String idStr = JOptionPane.showInputDialog(this,
                "Digite o ID da atividade:", "Buscar Atividade",
                JOptionPane.QUESTION_MESSAGE);

        if (idStr == null || idStr.isBlank()) return;

        try {
            int id = Integer.parseInt(idStr);
            Optional<Atividade> opt = AtividadeDAO.buscarPorId(id);

            if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Atividade não encontrada.",
                        "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Atividade a = opt.get();
            JOptionPane.showMessageDialog(this,
                    "📘 Atividade: " + a.getNome() +
                            "\nDescrição: " + a.getDesc() +
                            "\nInício: " + a.getInicio() +
                            "\nFim: " + a.getFim() +
                            "\nValor: " + a.getValor(),
                    "Detalhes da Atividade",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID inválido. Digite um número.",
                    "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar atividade: " + e.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
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