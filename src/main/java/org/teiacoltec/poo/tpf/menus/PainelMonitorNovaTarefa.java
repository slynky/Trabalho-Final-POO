package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.conexao.dao.TarefaDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PainelMonitorNovaTarefa extends JPanel {

    private JComboBox<Atividade> comboAtividades;
    private JTextField txtNome;
    private JTextField txtValor;
    private Turma turma;

    public PainelMonitorNovaTarefa(MainFrame frame, Monitor monitor, Turma turma) {
        this.turma = turma;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("Registrar Nova Tarefa", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        comboAtividades = new JComboBox<>();
        txtNome = new JTextField();
        txtValor = new JTextField();

        form.add(new JLabel("Atividade Base:"));
        form.add(comboAtividades);
        form.add(new JLabel("Nome da Tarefa:"));
        form.add(txtNome);
        form.add(new JLabel("Valor da Nota:"));
        form.add(txtValor);

        add(form, BorderLayout.CENTER);

        // Botões
        JPanel botoes = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnVoltar = new JButton("Voltar");

        btnSalvar.setBackground(new Color(46, 125, 50));
        btnSalvar.setForeground(Color.WHITE);

        botoes.add(btnSalvar);
        botoes.add(btnVoltar);
        add(botoes, BorderLayout.SOUTH);

        // Eventos
        btnVoltar.addActionListener(e -> frame.trocarPainel("MONITOR"));
        btnSalvar.addActionListener(e -> salvar());

        carregarAtividades();
    }

    private void carregarAtividades() {
        try {
            List<Atividade> lista = AtividadeDAO.listarTodas();
            for (Atividade a : lista) comboAtividades.addItem(a);
        } catch (Exception e) {}
    }

    private void salvar() {
        try {
            Atividade atv = (Atividade) comboAtividades.getSelectedItem();
            String nome = txtNome.getText();
            float valor = Float.parseFloat(txtValor.getText());

            Tarefa t = new Tarefa(0, nome, turma, atv, valor);
            TarefaDAO.inserirTarefa(t);

            JOptionPane.showMessageDialog(this, "Tarefa cadastrada!");
            txtNome.setText("");
            txtValor.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}