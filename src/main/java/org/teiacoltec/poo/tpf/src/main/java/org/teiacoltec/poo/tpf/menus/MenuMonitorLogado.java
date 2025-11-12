package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MenuMonitorLogado extends JPanel {

    private final Monitor monitor;

    public MenuMonitorLogado(MainFrame frame, Monitor monitor) {
        this.monitor = monitor;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 🔹 Título
        JLabel titulo = new JLabel("Painel do Monitor - " + monitor.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#1B5E20"));
        add(titulo, BorderLayout.NORTH);

        // 🔹 Painel de botões
        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnVisualizarAlunos = criarBotao("Visualizar Alunos da Turma");
        JButton btnVerAtividades = criarBotao("Atividades da Turma");
        JButton btnRegistrarAtividade = criarBotao("Registrar Nova Atividade");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnVisualizarAlunos);
        botoes.add(btnVerAtividades);
        botoes.add(btnRegistrarAtividade);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        // 🔹 Ações dos botões
        btnVisualizarAlunos.addActionListener(e -> visualizarAlunos());
        btnVerAtividades.addActionListener(e -> visualizarAtividades());
        btnRegistrarAtividade.addActionListener(e -> registrarAtividade());
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    /**
     * Mostra os alunos da turma monitorada.
     */
    private void visualizarAlunos() {
        try {
            TurmaDAO turmaDAO = new TurmaDAO();
            Turma turma = turmaDAO.buscarTurmaPorMonitor(monitor.getId());

            if (turma == null) {
                JOptionPane.showMessageDialog(this,
                        "Você ainda não está vinculado a nenhuma turma.",
                        "Sem Turma", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            AlunoDAO alunoDAO = new AlunoDAO();
            List<Aluno> alunos = alunoDAO.listarAlunosPorTurma(turma.getId());

            if (alunos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhum aluno encontrado na turma " + turma.getNome() + ".",
                        "Turma Vazia", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("👩‍🎓 Alunos da turma " + turma.getNome() + ":\n\n");
            for (Aluno a : alunos) {
                sb.append("- ").append(a.getNome()).append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Alunos da Turma", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar alunos: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exibe as atividades associadas à turma monitorada.
     */
    private void visualizarAtividades() {
        try {
            TurmaDAO turmaDAO = new TurmaDAO();
            Turma turma = turmaDAO.buscarTurmaPorMonitor(monitor.getId());

            if (turma == null) {
                JOptionPane.showMessageDialog(this,
                        "Você ainda não está vinculado a nenhuma turma.",
                        "Sem Turma", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            AtividadeDAO atividadeDAO = new AtividadeDAO();
            var atividades = atividadeDAO.listarAtividadesPorTurma(turma.getId());

            if (atividades.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma atividade cadastrada para a turma " + turma.getNome() + ".",
                        "Atividades", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("📚 Atividades da turma " + turma.getNome() + ":\n\n");
            atividades.forEach(a -> sb.append("- ").append(a.getTitulo()).append("\n"));

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Atividades da Turma", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar atividades: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Permite registrar uma nova atividade na turma do monitor.
     */
    private void registrarAtividade() {
        try {
            TurmaDAO turmaDAO = new TurmaDAO();
            Turma turma = turmaDAO.buscarTurmaPorMonitor(monitor.getId());

            if (turma == null) {
                JOptionPane.showMessageDialog(this,
                        "Você não está vinculado a nenhuma turma.",
                        "Sem Turma", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String titulo = JOptionPane.showInputDialog(this,
                    "Digite o título da nova atividade:");

            if (titulo == null || titulo.isBlank()) return;

            String descricao = JOptionPane.showInputDialog(this,
                    "Digite a descrição da atividade:");

            AtividadeDAO atividadeDAO = new AtividadeDAO();
            atividadeDAO.inserirAtividade(titulo, descricao, turma.getId());

            JOptionPane.showMessageDialog(this,
                    "✅ Atividade registrada com sucesso!",
                    "Nova Atividade", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao registrar atividade: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cria botões estilizados com hover.
     */
    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(Color.decode("#43A047"));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
