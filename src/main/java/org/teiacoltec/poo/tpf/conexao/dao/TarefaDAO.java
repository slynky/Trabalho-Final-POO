package org.teiacoltec.poo.tpf.conexao.dao;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;


public class TarefaDAO {
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void inserirTarefa(Tarefa tarefa) throws SQLException {
        String sqlTarefa = "INSERT INTO Tarefa (id, nome, id_turma, id_atividade, nota) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlTarefa)) {

                stmt.setInt(1, tarefa.getId()); // Define o ID
                stmt.setString(2, tarefa.getNome());
                stmt.setInt(3, tarefa.getTurmaId());
                stmt.setInt(4, tarefa.getAtividadeId());
                stmt.setFloat(5, tarefa.getNota());

                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    conn.commit();
                } else {
                    throw new SQLException("Falha ao inserir tarefa, nenhuma linha afetada.");
                }

            } catch (SQLIntegrityConstraintViolationException e) {
                //Lógica de UPSERT: Se ID duplicado (chave primária), atualiza
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("PRIMARY")) {
                    conn.rollback();
                    System.out.println("Tarefa com ID " + tarefa.getId() + " já existe. Chamando atualização.");
                    atualizarTarefa(tarefa);
                    return;
                }
                throw e;

            } catch (SQLException e) {
                System.err.println("Erro ao inserir tarefa. Revertendo transacao. " + e.getMessage());
                if (conn != null) conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static void atualizarTarefa(Tarefa tarefa) throws SQLException {
        String sqlTarefa = "UPDATE Tarefa SET nome = ?, id_turma = ?, id_atividade = ?, nota = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sqlTarefa)) {

                stmt.setString(1, tarefa.getNome());
                stmt.setInt(2, tarefa.getTurmaId());
                stmt.setInt(3, tarefa.getAtividadeId());
                stmt.setFloat(4, tarefa.getNota());
                stmt.setInt(5, tarefa.getId()); // WHERE id = ?

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    conn.commit();
                } else {
                    throw new SQLException("Falha ao atualizar tarefa (ID: " + tarefa.getId() + "), nenhuma linha afetada.");
                }

            } catch (SQLException e) {
                System.err.println("Erro ao atualizar tarefa. Revertendo transacao. " + e.getMessage());
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static Optional<Tarefa> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Tarefa WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int turmaIdParaConstrutor = rs.getInt("id_turma");
                    int atividadeIdParaConstrutor = rs.getInt("id_atividade");
                    Optional<Turma> optTurma = TurmaDAO.obterTurmaPorId(turmaIdParaConstrutor);
                    Optional<Atividade> optAtividade = AtividadeDAO.buscarPorId(atividadeIdParaConstrutor);

                    if (optTurma.isEmpty() || optAtividade.isEmpty()) {
                        //Se a FK for válida mas a linha não for encontrada, algo está errado no BD.
                        return Optional.empty();
                    }

                    Turma turma = optTurma.get();
                    Atividade atividade = optAtividade.get();

                    Tarefa tarefa = new Tarefa(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            turma,
                            atividade,
                            rs.getFloat("nota")
                    );
                    return Optional.of(tarefa);
                }
                return Optional.empty();
            }
        }
    }

    public static void removerTarefa(int id) throws SQLException {
        String sqlTarefa = "DELETE FROM Tarefa WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmtTarefa = conn.prepareStatement(sqlTarefa)){
            stmtTarefa.setInt(1, id);
            stmtTarefa.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<Tarefa> listarTarefasPorTurma(int idTurma) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT id FROM Tarefa WHERE id_turma = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTurma);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    buscarPorId(rs.getInt("id")).ifPresent(tarefas::add);
                }
            }
        }
        return tarefas;
    }

    public static void lancarNota(int idTarefa, String cpfAluno, float nota) throws SQLException {
        // Verifica se já existe nota. Se sim, atualiza. Se não, insere.
        // Maneira simples compatível com MySQL: DELETE antigo e INSERT novo (ou UPDATE direto)
        // Vamos usar uma lógica simples: Tenta UPDATE, se afetar 0 linhas, faz INSERT.

        String sqlUpdate = "UPDATE Nota_Aluno SET nota_obtida = ? WHERE id_tarefa = ? AND cpf_aluno = ?";
        String sqlInsert = "INSERT INTO Nota_Aluno (id_tarefa, cpf_aluno, nota_obtida) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()) {
            try (PreparedStatement stmtUpd = conn.prepareStatement(sqlUpdate)) {
                stmtUpd.setFloat(1, nota);
                stmtUpd.setInt(2, idTarefa);
                stmtUpd.setString(3, cpfAluno);
                int linhas = stmtUpd.executeUpdate();

                if (linhas == 0) { // Não existia nota, vamos inserir
                    try (PreparedStatement stmtIns = conn.prepareStatement(sqlInsert)) {
                        stmtIns.setInt(1, idTarefa);
                        stmtIns.setString(2, cpfAluno);
                        stmtIns.setFloat(3, nota);
                        stmtIns.executeUpdate();
                    }
                }
            }
        }
    }
}