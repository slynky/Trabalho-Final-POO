package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.TurmaDAO;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PainelGerenciarTurmas extends JPanel {

    private final MainFrame frame;
    private final Professor professor;
    private JTable tabelaTurmas;
    private DefaultTableModel modeloTabela;

    public PainelGerenciarTurmas(MainFrame frame, Professor professor) {
        this.frame = frame;
        this.professor = professor;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F5F7FA"));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. Título ---
        JLabel lblTitulo = new JLabel("Gerenciamento de Turmas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.decode("#0D47A1"));
        add(lblTitulo, BorderLayout.NORTH);

        // --- 2. Tabela de Turmas ---
        String[] colunas = {"ID", "Nome da Turma"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela somente leitura
            }
        };

        tabelaTurmas = new JTable(modeloTabela);
        tabelaTurmas.setRowHeight(30);
        tabelaTurmas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaTurmas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Ajuste de largura das colunas (opcional)
        tabelaTurmas.getColumnModel().getColumn(0).setPreferredWidth(50); // ID menor
        tabelaTurmas.getColumnModel().getColumn(1).setPreferredWidth(200); // Nome maior

        JScrollPane scrollPane = new JScrollPane(tabelaTurmas);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Botões de Ação ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setOpaque(false);

        JButton btnNova = criarBotao("Nova Turma", new Color(46, 125, 50)); // Verde
        JButton btnExcluir = criarBotao("Excluir Selecionada", new Color(198, 40, 40)); // Vermelho
        JButton btnVoltar = criarBotao("Voltar", Color.GRAY);

        painelBotoes.add(btnNova);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnVoltar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- 4. Eventos (Listeners) ---
        btnVoltar.addActionListener(e -> frame.trocarPainel("PROFESSOR"));
        btnNova.addActionListener(e -> abrirDialogoNovaTurma());
        btnExcluir.addActionListener(e -> excluirTurmaSelecionada());

        // Carrega os dados assim que a tela é criada
        atualizarTabela();
    }

    /**
     * Busca as turmas do banco e preenche a tabela.
     */
    private void atualizarTabela() {
        modeloTabela.setRowCount(0); // Limpa a tabela visual
        try {
            List<Turma> turmas = TurmaDAO.listarTurmasPorProfessor(professor.getCpf());

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Turma t : turmas) {
                Object[] linha = {
                        t.getId(),
                        t.getNome(),
                        t.getInicio().format(String.valueOf(fmt)),
                        t.getFim().format(String.valueOf(fmt)),
                        t.getDesc()
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar turmas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre um formulário (JOptionPane) para criar uma turma.
     */
    private void abrirDialogoNovaTurma() {
        JTextField txId = new JTextField();
        JTextField txtNome = new JTextField();
        JTextField txtDesc = new JTextField();
        JTextField txtInicio = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField txtFim = new JTextField(LocalDate.now().plusMonths(6).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        Object[] inputs = {
                "ID:", txId,
                "Nome da Turma:", txtNome,
                "Descrição:", txtDesc,
                "Data Início (dd/MM/yyyy):", txtInicio,
                "Data Fim (dd/MM/yyyy):", txtFim
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Nova Turma", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            if (txId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O ID da turma é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome da turma é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {

                int id;
                try {
                    id = Integer.parseInt(txId.getText().trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "O ID deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String nome = txtNome.getText().trim();
                String desc = txtDesc.getText().trim();
                String dataInicio = txtInicio.getText().trim();
                String dataFim = txtFim.getText().trim();

                ArrayList<Pessoa> participantes = new ArrayList<>();
                participantes.add(professor);

                Turma novaTurma = new Turma(id, nome, desc, dataInicio, dataFim, participantes, null);

                // Salvar no Banco
                TurmaDAO.inserirTurma(novaTurma);

                JOptionPane.showMessageDialog(this, "Turma criada com sucesso!");
                atualizarTabela();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao criar turma: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * Exclui a turma selecionada na tabela.
     */
    private void excluirTurmaSelecionada() {
        int linhaSelecionada = tabelaTurmas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma turma na tabela para excluir.");
            return;
        }

        int idTurma = (int) tabelaTurmas.getValueAt(linhaSelecionada, 0);
        String nomeTurma = (String) tabelaTurmas.getValueAt(linhaSelecionada, 1);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a turma '" + nomeTurma + "'?\nIsso apagará todas as tarefas e vínculos.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                TurmaDAO.removerTurmaPorId(idTurma);
                atualizarTabela();
                JOptionPane.showMessageDialog(this, "Turma excluída.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton criarBotao(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
