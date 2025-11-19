package org.teiacoltec.poo.tpf.menus;

import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class PainelAlunoAtividades extends JPanel {

    private MainFrame frame;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtBuscaId;

    public PainelAlunoAtividades(MainFrame frame, Aluno aluno) {
        this.frame = frame;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Consulta de Atividades", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.decode("#2C3E50"));
        painelTopo.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Buscar por ID:"));
        txtBuscaId = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Ver Todas");

        painelBusca.add(txtBuscaId);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnLimpar);
        painelTopo.add(painelBusca, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Atividade", "Descrição", "Valor"};

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Nenhuma célula é editável!
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Apenas uma linha por vez
        tabela.setFocusable(false); // Remove o foco para evitar navegação por teclado (opcional)

        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(300);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.addActionListener(e -> frame.trocarPainel("ALUNO"));
        add(btnVoltar, BorderLayout.SOUTH);

        btnLimpar.addActionListener(e -> carregarTodas());
        btnBuscar.addActionListener(e -> buscarPorId());

        carregarTodas();
    }

    private void carregarTodas() {
        modelo.setRowCount(0);
        txtBuscaId.setText("");
        try {
            List<Atividade> lista = AtividadeDAO.listarTodas();
            for (Atividade a : lista) {
                adicionarLinha(a);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void buscarPorId() {
        String idStr = txtBuscaId.getText().trim();
        if (idStr.isEmpty()) return;

        try {
            int id = Integer.parseInt(idStr);
            Optional<Atividade> opt = AtividadeDAO.buscarPorId(id);

            modelo.setRowCount(0);
            if (opt.isPresent()) {
                adicionarLinha(opt.get());
            } else {
                JOptionPane.showMessageDialog(this, "Atividade ID " + id + " não encontrada.");
                carregarTodas();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um número válido.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void adicionarLinha(Atividade a) {
        modelo.addRow(new Object[]{
                a.getId(), a.getNome(), a.getDesc(), a.getValor()
        });
    }
}