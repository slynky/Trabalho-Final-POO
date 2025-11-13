package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AlunoDAO;
import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MenuMonitorLogado extends JPanel {

    private final Monitor monitor;
    private Turma turmaMonitorada;

    public MenuMonitorLogado(MainFrame frame, Monitor monitor) {
        this.monitor = monitor;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("Painel do Monitor - " + monitor.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#1B5E20"));
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnVisualizarAlunos = criarBotao("Visualizar Alunos da Turma");
        JButton btnVerAtividades = criarBotao("Tarefas da Turma");
        JButton btnRegistrarTarefa = criarBotao("Registrar Nova Tarefa");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnVisualizarAlunos);
        botoes.add(btnVerAtividades);
        botoes.add(btnRegistrarTarefa);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        btnVisualizarAlunos.addActionListener(e -> visualizarAlunos());
        btnVerAtividades.addActionListener(e -> visualizarTarefas());
        btnRegistrarTarefa.addActionListener(e -> registrarTarefa());
        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));

        carregarTurmaMonitor();
    }

    private void carregarTurmaMonitor() {
        try {
            List<Turma> turmas = TurmaDAO.listarTurmasPorCpfMonitor(monitor.getCpf());

            if (turmas.isEmpty()) {
                this.turmaMonitorada = null;
                JOptionPane.showMessageDialog(this,
                        "Aviso: Você não está alocado a nenhuma turma.",
                        "Sem Turma", JOptionPane.WARNING_MESSAGE);
            } else {
                // Pega a primeira turma da lista.
                this.turmaMonitorada = turmas.get(0);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar sua turma: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void visualizarAlunos() {
        if (turmaMonitorada == null) {
            JOptionPane.showMessageDialog(this, "Você não está vinculado a nenhuma turma.", "Sem Turma", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            List<Aluno> alunos = AlunoDAO.listarTodosAlunosDaTurma(turmaMonitorada.getId());

            if (alunos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhum aluno encontrado na turma " + turmaMonitorada.getNome() + ".",
                        "Turma Vazia", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("Alunos da turma " + turmaMonitorada.getNome() + ":\n\n");
            for (Aluno a : alunos) {
                sb.append("- ").append(a.getNome()).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(350, 250));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Alunos da Turma", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar alunos: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exibe as tarefas (atividades associadas) à turma monitorada.
     */
    private void visualizarTarefas() {
        if (turmaMonitorada == null) {
            JOptionPane.showMessageDialog(this, "Você não está vinculado a nenhuma turma.", "Sem Turma", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // <<< CORREÇÃO >>>
            // O que queremos ver são as TAREFAS (vínculo de Atividade com Turma),
            // não as Atividades genéricas.
            List<Tarefa> tarefas = TarefaDAO.listarTarefasPorTurma(turmaMonitorada.getId());

            if (tarefas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma tarefa cadastrada para a turma " + turmaMonitorada.getNome() + ".",
                        "Tarefas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("📚 Tarefas da turma " + turmaMonitorada.getNome() + ":\n\n");
            tarefas.forEach(t -> sb.append("- ").append(t.getNome())
                    .append(" (Atividade: ").append(t.getNome())
                    .append(" | Valor: ").append(t.getNota()).append(")\n"));

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Tarefas da Turma", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar tarefas: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarTarefa() {
        if (turmaMonitorada == null) {
            JOptionPane.showMessageDialog(this, "Você não está vinculado a nenhuma turma.", "Sem Turma", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String idAtividadeStr = JOptionPane.showInputDialog(this, "Digite o ID da Atividade global a ser usada:");
            if (idAtividadeStr == null || idAtividadeStr.isBlank()) return;
            int idAtividade = Integer.parseInt(idAtividadeStr);

            //Verifica se a Atividade existe
            Optional<Atividade> optAtividade = AtividadeDAO.buscarPorId(idAtividade);
            if (optAtividade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma Atividade encontrada com este ID.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Atividade atividadeBase = optAtividade.get();

            String nomeTarefa = JOptionPane.showInputDialog(this, "Dê um nome para esta Tarefa (ex: P1 - Turma da Manhã):", atividadeBase.getNome());
            if (nomeTarefa == null || nomeTarefa.isBlank()) return;

            String valorTarefaStr = JOptionPane.showInputDialog(this, "Qual o valor (nota) desta Tarefa?", Atividade.getValor());
            if (valorTarefaStr == null || valorTarefaStr.isBlank()) return;
            float valorTarefa = Float.parseFloat(valorTarefaStr);

            Tarefa novaTarefa = new Tarefa(
                    0,
                    nomeTarefa,
                    turmaMonitorada,
                    atividadeBase,
                    valorTarefa
            );

            TarefaDAO.inserirTarefa(novaTarefa);

            JOptionPane.showMessageDialog(this,
                    "Tarefa registrada com sucesso!",
                    "Nova Tarefa", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor ou ID inválido. Por favor, digite números.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao registrar tarefa: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

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