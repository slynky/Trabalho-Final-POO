package org.teiacoltec.poo.tpf.conexao.dao;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

public class TurmaDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");



    public static void inserirTurma(Turma turma) throws SQLException {
        String sqlTurma = "INSERT INTO Turma (nome, descricao, data_inicio, data_fim, id_turma_pai) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stm = conn.prepareStatement(sqlTurma, Statement.RETURN_GENERATED_KEYS)) {

                stm.setString(1, turma.getNome());
                stm.setString(2, turma.getDesc());
                stm.setDate(3, Date.valueOf(turma.getInicio()));
                stm.setDate(4, Date.valueOf(turma.getFim()));
                if (turma.getTurmaPai() != null) {
                    stm.setInt(5, turma.getTurmaPai().getId());
                } else {
                    stm.setNull(5, java.sql.Types.INTEGER);
                }

                int linhasAfetadas = stm.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao inserir turma, nenhuma linha afetada.");
                }

                int novoIdTurma = -1;
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        novoIdTurma = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter o ID da turma inserida.");
                    }
                }

                for (Pessoa p : turma.getParticipantes()) {
                    inserirParticipante(conn, p.getCpf(), novoIdTurma);
                }

                conn.commit();

            } catch (SQLException e) {
                System.err.println("Erro ao inserir Turma. Revertendo processo. Erro: " + e.getMessage());
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

    private static void inserirParticipante(Connection conn, String cpf, int idTurma) throws SQLException {
        String sqlInserir = "INSERT INTO Turma_Participantes (cpf_pessoa, id_turma) VALUES (?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sqlInserir)) {
            stmt.setString(1, cpf);
            stmt.setInt(2, idTurma);
            stmt.executeUpdate();
        }
    }

    public static void inserirParticipante(String cpf, int idTurma) throws SQLException {
        String sqlInserir = "INSERT INTO Turma_Participantes (cpf_pessoa, id_turma) VALUES (?,?)";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInserir)) {

            stmt.setString(1, cpf);
            stmt.setInt(2, idTurma);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir participante: " + e.getMessage());
            throw e;
        }
    }

    public static void removerParticipante(String cpf, int idTurma) throws SQLException {
        String sqlRemover = "DELETE FROM Turma_Participantes WHERE cpf_pessoa = ? AND id_turma = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRemover)) {

            stmt.setString(1, cpf);
            stmt.setInt(2, idTurma);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao remover participante da turma. Erro: " + e.getMessage());
            throw e;
        }
    }

    public static Optional<Turma> obterTurmaPorId(int id) throws SQLException {

        String sqlTurma = "SELECT * FROM Turma WHERE id = ?";
        String sqlParticipantes = "SELECT tp.cpf_pessoa, p.senha FROM Turma_Participantes tp JOIN Pessoa p ON p.cpf = tp.cpf_pessoa WHERE tp.id_turma = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmtTurma = conn.prepareStatement(sqlTurma)) {

            stmtTurma.setInt(1, id);

            try (ResultSet rs = stmtTurma.executeQuery()) {

                if (rs.next()) {

                    ArrayList<Pessoa> participantes = new ArrayList<>();

                    try (PreparedStatement stmtParticipantes = conn.prepareStatement(sqlParticipantes)) {

                        stmtParticipantes.setInt(1, id);

                        try (ResultSet rs2 = stmtParticipantes.executeQuery()) {

                            while (rs2.next()) {

                                String cpfParticipante = rs2.getString("cpf_pessoa");
                                String senhaParticipante = rs2.getString("senha");

                                PessoaDAO.buscarPessoaPorCpf(cpfParticipante, senhaParticipante)
                                        .ifPresent(participantes::add);
                            }
                        }
                    }

                    String dataInicio = rs.getDate("data_inicio").toLocalDate().format(FORMATADOR);
                    String dataFim = rs.getDate("data_fim").toLocalDate().format(FORMATADOR);

                    int idTurmaPai = rs.getInt("id_turma_pai");
                    Turma tPai = null;
                    if (!rs.wasNull() && idTurmaPai > 0) {
                        tPai = TurmaDAO.obterTurmaPorId(idTurmaPai).orElse(null);
                    }

                    Turma turma = new Turma(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            dataInicio,
                            dataFim,
                            participantes,
                            tPai
                    );

                    return Optional.of(turma);
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw e;
        }
    }
    public static void removerTurmaPorId(int id) throws SQLException {
        String sqlTurma = "DELETE FROM Turma WHERE id = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmtTurma = conn.prepareStatement(sqlTurma)
                ) {
            stmtTurma.setInt(1, id);
            stmtTurma.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
