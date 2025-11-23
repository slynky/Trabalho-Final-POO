package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PainelAssociarAtividade extends JPanel {

    private MainFrame frame;
    private Professor professor;
    private JComboBox<Turma> comboTurmas;
    private JComboBox<Atividade> comboAtividades;
    private JTextField txtNomeTarefa;
    private JTextField txtValor;

    public PainelAssociarAtividade(MainFrame frame, Professor professor) {
        this.frame = frame;
        this.professor = professor;

        setLayout(new BorderLayout(20, 20));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("Associar Atividade à Turma", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.decode("#0D47A1"));
        add(titulo, BorderLayout.NORTH);

        // --- Formulário ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setOpaque(false);

        comboTurmas = new JComboBox<>();
        comboAtividades = new JComboBox<>();
        txtNomeTarefa = new JTextField();
        txtValor = new JTextField();

        formPanel.add(new JLabel("Selecione a Turma:"));
        formPanel.add(comboTurmas);
        formPanel.add(new JLabel("Atividade Base:"));
        formPanel.add(comboAtividades);
        formPanel.add(new JLabel("Nome personalizado da Tarefa:"));
        formPanel.add(txtNomeTarefa);
        formPanel.add(new JLabel("Valor (Nota):"));
        formPanel.add(txtValor);

        add(formPanel, BorderLayout.CENTER);

        // --- Botões ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Associação");
        JButton btnVoltar = new JButton("Voltar");

        btnSalvar.setBackground(Color.decode("#2E7D32"));
        btnSalvar.setForeground(Color.WHITE);

        btnPanel.add(btnSalvar);
        btnPanel.add(btnVoltar);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Ações ---
        btnVoltar.addActionListener(e -> frame.trocarPainel("PROFESSOR"));
        btnSalvar.addActionListener(e -> salvarTarefa());

        carregarDados();
    }

    private void carregarDados() {
        try {
            comboTurmas.removeAllItems();
            comboAtividades.removeAllItems();

            List<Turma> turmas = TurmaDAO.listarTurmasPorProfessor(professor.getCpf());
            for (Turma t : turmas) comboTurmas.addItem(t);

            List<Atividade> atividades = AtividadeDAO.listarTodas();
            for (Atividade a : atividades) comboAtividades.addItem(a);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void salvarTarefa() {
        try {
            Turma turmaSel = (Turma) comboTurmas.getSelectedItem();
            Atividade atvSel = (Atividade) comboAtividades.getSelectedItem();
            String nome = txtNomeTarefa.getText();
            float valor = Float.parseFloat(txtValor.getText().replace(",", "."));

            if (turmaSel == null || atvSel == null || nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            // Cria a tarefa
            Tarefa novaTarefa = new Tarefa(0, nome, turmaSel, atvSel, valor);
            TarefaDAO.inserirTarefa(novaTarefa);

            JOptionPane.showMessageDialog(this, "Atividade associada com sucesso!");
            txtNomeTarefa.setText("");
            txtValor.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }
}