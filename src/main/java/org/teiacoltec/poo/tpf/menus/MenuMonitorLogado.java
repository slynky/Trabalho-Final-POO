package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MenuMonitorLogado extends JPanel {

    private final Monitor monitor;
    private Turma turmaMonitorada;

    public MenuMonitorLogado(MainFrame frame, Monitor monitor) {
        this.monitor = monitor;

        // Busca a turma logo no início para passar para os sub-painéis
        carregarTurmaMonitor();

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("Painel do Monitor - " + monitor.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.decode("#1B5E20"));
        add(titulo, BorderLayout.NORTH);

        if (turmaMonitorada != null) {
            JLabel lblTurma = new JLabel("Turma Atual: " + turmaMonitorada.getNome(), SwingConstants.CENTER);
            lblTurma.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            add(lblTurma, BorderLayout.CENTER); // Apenas para mostrar algo no centro se quiser
        }

        JPanel botoes = new JPanel(new GridLayout(4, 1, 15, 15));
        botoes.setOpaque(false);

        JButton btnAlunos = criarBotao("Visualizar Alunos da Turma");
        JButton btnTarefas = criarBotao("Ver Tarefas da Turma");
        JButton btnNovaTarefa = criarBotao("Registrar Nova Tarefa");
        JButton btnSair = criarBotao("Sair");

        botoes.add(btnAlunos);
        botoes.add(btnTarefas);
        botoes.add(btnNovaTarefa);
        botoes.add(btnSair);

        add(botoes, BorderLayout.CENTER);

        // NAVEGAÇÃO
        btnAlunos.addActionListener(e -> {
            if(validarTurma()) frame.trocarPainel("MONITOR_ALUNOS");
        });

        btnTarefas.addActionListener(e -> {
            if(validarTurma()) frame.trocarPainel("MONITOR_TAREFAS");
        });

        btnNovaTarefa.addActionListener(e -> {
            if(validarTurma()) frame.trocarPainel("MONITOR_NOVA_TAREFA");
        });

        btnSair.addActionListener(e -> frame.trocarPainel("LOGIN"));
    }

    private void carregarTurmaMonitor() {
        try {
            List<Turma> turmas = TurmaDAO.listarTurmasPorCpfMonitor(monitor.getCpf());
            if (!turmas.isEmpty()) {
                this.turmaMonitorada = turmas.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validarTurma() {
        if (turmaMonitorada == null) {
            JOptionPane.showMessageDialog(this, "Você não está vinculado a nenhuma turma.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
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