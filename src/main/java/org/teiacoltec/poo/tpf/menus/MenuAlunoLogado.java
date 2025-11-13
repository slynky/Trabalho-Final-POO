package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
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

        // 🔹 Cabeçalho
        JLabel titulo = new JLabel("Painel do Aluno - " + aluno.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#2C3E50"));
        add(titulo, BorderLayout.NORTH);

        // 🔹 Painel de botões
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

        // 🔹 Ações dos botões
        btnTurmas.addActionListener(e -> listarTurmasAluno());
        btnAtividades.addActionListener(e -> listarTodasAtividades());
        btnBuscarAtividade.addActionListener(e -> buscarAtividadePorId());
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    /**
     * Lista todas as turmas nas quais o aluno está matriculado.
     */
    private void listarTurmasAluno() {
        try {
            // supondo que o aluno está matriculado via tabela Turma_Participantes
            List<String> nomesTurmas = org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO.listarTurmasPorCpfAluno(aluno.getCpf());

            if (nomesTurmas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Você ainda não está matriculado em nenhuma turma.",
                        "Minhas Turmas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("📘 Suas Turmas:\n\n");
            for (String nome : nomesTurmas) {
                sb.append("• ").append(nome).append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Turmas do Aluno", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar turmas: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lista todas as atividades registradas.
     */
    private void listarTodasAtividades() {
        try {
            String sql = "SELECT id FROM Atividade";
            List<Integer> ids = org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO.buscarTodosIdsAtividades();

            if (ids.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma atividade cadastrada no sistema.",
                        "Atividades", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("📝 Todas as Atividades:\n\n");
            for (int id : ids) {
                Optional<Atividade> atv = AtividadeDAO.buscarPorId(id);
                atv.ifPresent(a -> sb.append("- ").append(a.getNome()).append(" (Valor: ").append(a.getValor()).append(")\n"));
            }

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Atividades", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar atividades: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Permite ao aluno consultar uma atividade específica pelo ID.
     */
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

    /**
     * Cria um botão estilizado com hover.
     */
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
