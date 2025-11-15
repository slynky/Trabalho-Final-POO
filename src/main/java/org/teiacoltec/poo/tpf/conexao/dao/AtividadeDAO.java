package org.teiacoltec.poo.tpf.conexao.dao;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.escolares.Atividade;

public class AtividadeDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");



    public static void inserirAtividade(Atividade atividade) throws SQLException {

        String sqlAtividade = "INSERT INTO Atividade (id, nome, descricao, data_inicio, data_fim, valor) " + "VALUES (?, ?, ?, ?, ?, ?)"; // Adicionei 'id' na query

        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sqlAtividade)) {

                stmt.setInt(1, atividade.getId()); // Adicione o ID aqui
                stmt.setString(2, atividade.getNome());
                stmt.setString(3, atividade.getDesc());
                stmt.setDate(4, Date.valueOf(atividade.getInicio()));
                stmt.setDate(5, Date.valueOf(atividade.getFim()));
                stmt.setFloat(6, atividade.getValor());

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    conn.commit();
                } else {
                    throw new SQLException("Falha ao inserir atividade, nenhuma linha afetada.");
                }

            } catch (SQLIntegrityConstraintViolationException e) {
                // Se a exceção for devido a ID duplicado (chave primária), atualiza.
                if (e.getMessage().contains("Duplicate entry")) {
                    conn.rollback();
                    System.out.println("Atividade com ID " + atividade.getId() + " já existe. Chamando atualização.");
                    // Chama o método de atualização já implementado
                    atualizarAtividade(atividade);
                    return;
                }
                throw e;

            } catch (SQLException e) {
                System.err.println("Erro ao inserir atividade. Revertendo transacao");
                conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    public static void atualizarAtividade(Atividade atividade) throws SQLException {
        String sqlAtividade = "UPDATE Atividade SET nome = ?, descricao = ?, data_inicio = ?, data_fim = ?, valor = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sqlAtividade)) {

                stmt.setString(1, atividade.getNome());
                stmt.setString(2, atividade.getDesc());
                stmt.setDate(3, Date.valueOf(atividade.getInicio()));
                stmt.setDate(4, Date.valueOf(atividade.getFim()));
                stmt.setFloat(5, atividade.getValor());
                stmt.setInt(6, atividade.getId()); // WHERE id = ?

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    conn.commit();
                } else {
                    throw new SQLException("Falha ao atualizar atividade (ID: " + atividade.getId() + "), nenhuma linha afetada.");
                }

            } catch (SQLException e) {
                System.err.println("Erro ao atualizar atividade. Revertendo transacao. " + e.getMessage());
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static Optional<Atividade> buscarPorId(int id) throws SQLException{
        String sqlAtividade = "SELECT * FROM Atividade WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlAtividade)){

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    String dataInicio = rs.getDate("data_inicio").toLocalDate().format(FORMATADOR);
                    String dataFim = rs.getDate("data_fim").toLocalDate().format(FORMATADOR);

                    Atividade atividade = new Atividade(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            dataInicio,
                            dataFim,
                            rs.getFloat("valor")
                    );
                    return Optional.of(atividade);
                }
            }
        }

        return Optional.empty();
    }

    public static void atualizarNome(int id, String novoNome) throws SQLException {
        String sqlAtividade = "UPDATE Atividade SET nome = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmtAtividade = conn.prepareStatement(sqlAtividade)) {
            stmtAtividade.setString(1, novoNome);
            stmtAtividade.setInt(2, id);
            stmtAtividade.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }
    }

    public static void removerAtividade(int id) throws SQLException {
        String sqlAtividade = "DELETE FROM Atividade WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmtAtividade = conn.prepareStatement(sqlAtividade)) {

            stmtAtividade.setInt(1, id);
            stmtAtividade.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<Atividade> listarTodas() throws SQLException {
        List<Atividade> atividades = new ArrayList<>();
        String sql = "SELECT * FROM Atividade";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dataInicio = rs.getDate("data_inicio").toLocalDate().format(FORMATADOR);
                String dataFim = rs.getDate("data_fim").toLocalDate().format(FORMATADOR);

                Atividade atividade = new Atividade(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        dataInicio,
                        dataFim,
                        rs.getFloat("valor")
                );
                atividades.add(atividade);
            }
        }
        return atividades;
    }

}
