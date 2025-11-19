package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.pessoa.*;

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

        // Passa a lista e o frame para o LoginPanel
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

            // Registra sub-telas do Professor
            mainPanel.add(new PainelGerenciarTurmas(this, professor), "GERENCIAR_TURMAS");
            mainPanel.add(new PainelAssociarAtividade(this, professor), "ASSOCIAR_ATIVIDADES");
            mainPanel.add(new PainelLancarNotas(this, professor), "LANCAR_NOTAS_PROF");
            mainPanel.add(new PainelVisualizarParticipantes(this, professor), "VISUALIZAR_PARTICIPANTES");

            trocarPainel("PROFESSOR");

        } else if (usuario instanceof Aluno aluno) {
            painelAluno = new MenuAlunoLogado(this, aluno);
            mainPanel.add(painelAluno, "ALUNO");

            mainPanel.add(new PainelAlunoTurmas(this, aluno), "ALUNO_TURMAS");
            mainPanel.add(new PainelAlunoAtividades(this, aluno), "ALUNO_ATIVIDADES");

            trocarPainel("ALUNO");

        } else if (usuario instanceof Monitor monitor) {
            painelMonitor = new MenuMonitorLogado(this, monitor);
            mainPanel.add(painelMonitor, "MONITOR");

            try {
                List<Turma> t = TurmaDAO.listarTurmasPorCpfMonitor(monitor.getCpf());
                if (!t.isEmpty()) {
                    Turma turma = t.get(0);
                    mainPanel.add(new PainelMonitorAlunos(this, monitor, turma), "MONITOR_ALUNOS");
                    mainPanel.add(new PainelMonitorTarefas(this, monitor, turma), "MONITOR_TAREFAS");
                    mainPanel.add(new PainelMonitorNovaTarefa(this, monitor, turma), "MONITOR_NOVA_TAREFA");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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