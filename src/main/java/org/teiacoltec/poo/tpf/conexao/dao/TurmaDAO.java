package org.teiacoltec.poo.tpf.conexao.dao;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;

public class TurmaDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static void atualizarTurma(Turma turma) throws SQLException {
        String sqlTurma = "UPDATE Turma SET nome = ?, descricao = ?, data_inicio = ?, data_fim = ?, id_turma_pai = ? WHERE id = ?";

        // SQL para remover todos os participantes antigos
        String sqlRemoverParticipantes = "DELETE FROM Turma_Participantes WHERE id_turma = ?";

        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            // Inicia a transação
            conn.setAutoCommit(false);

            // 1. Atualiza os dados básicos da Turma
            try (PreparedStatement stm = conn.prepareStatement(sqlTurma)) {

                stm.setString(1, turma.getNome());
                stm.setString(2, turma.getDesc());
                // Converte LocalDate para java.sql.Date
                stm.setDate(3, Date.valueOf(turma.getInicio()));
                stm.setDate(4, Date.valueOf(turma.getFim()));

                if (turma.getTurmaPai() != null) {
                    stm.setInt(5, turma.getTurmaPai().getId());
                } else {
                    stm.setNull(5, java.sql.Types.INTEGER);
                }

                stm.setInt(6, turma.getId()); // WHERE id = ?

                int linhasAfetadas = stm.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao atualizar turma (ID " + turma.getId() + "), nenhuma linha afetada.");
                }
            }

            // 2. Sincroniza Participantes
            // 2a. Remove todos os participantes existentes para essa turma
            try (PreparedStatement stmRemover = conn.prepareStatement(sqlRemoverParticipantes)) {
                stmRemover.setInt(1, turma.getId());
                stmRemover.executeUpdate();
            }

            // 2b. Insere os participantes atualizados
            for (Pessoa p : turma.getParticipantes()) {
                // Insere ou atualiza a Pessoa (se necessário) - dependendo da lógica do PessoaDAO
                PessoaDAO.inserir(p);
                // Insere a relação Turma_Participantes
                inserirParticipante(conn, p.getCpf(), turma.getId());
            }

            // Confirma todas as alterações na transação
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar Turma. Revertendo processo. Erro: " + e.getMessage());
            if (conn != null) conn.rollback(); // Reverte em caso de erro
            throw e;
        } finally {
            if (conn != null) {
                // Volta para o modo de commit automático
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static void inserirTurma(Turma turma) throws SQLException {
        String sqlTurma = "INSERT INTO Turma (nome, descricao, data_inicio, data_fim, id_turma_pai) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false); // Inicia transação

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

                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int novoId = generatedKeys.getInt(1);
                        turma.setId(novoId); // Atualiza o objeto Java com o ID real do banco
                        System.out.println("DEBUG: Turma criada com ID: " + novoId);
                    } else {
                        throw new SQLException("Falha ao obter o ID da turma inserida.");
                    }
                }
            }

            for (Pessoa p : turma.getParticipantes()) {
                System.out.println("Tentando vincular CPF " + p.getCpf() + " à Turma " + turma.getId());

                if (turma.getId() <= 0) {
                    throw new SQLException("Erro Crítico: O ID da turma é inválido (" + turma.getId() + "). O vínculo falhará.");
                }

                inserirParticipante(conn, p.getCpf(), turma.getId());
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Erro na transação de inserir Turma: " + e.getMessage());
            if (conn != null) conn.rollback();
            throw e;
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

    // Dentro de TurmaDAO.java

    public static Optional<Turma> obterTurmaPorId(int id) throws SQLException {

        String sqlTurma = "SELECT * FROM Turma WHERE id = ?";
        // Alteração 1: A query de participantes não precisa mais da senha
        String sqlParticipantes = "SELECT tp.cpf_pessoa FROM Turma_Participantes tp WHERE tp.id_turma = ?";

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

                                PessoaDAO.buscarPessoaPorCpfSemValidacao(cpfParticipante)
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

    public static List<String> listarNomesTurmaPorCpf(String cpf) throws SQLException {
        List<String> nomes = new ArrayList<>();
        String sql = "SELECT t.nome FROM Turma t " +
                "JOIN Turma_Participantes tp ON t.id = tp.id_turma " +
                "WHERE tp.cpf_pessoa = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    nomes.add(rs.getString("nome"));
                }
            }
        }
        return nomes;
    }

    public static List<Turma> listarTurmasPorCpfMonitor(String cpf) throws SQLException {
        List<Turma> turmas = new ArrayList<>();
        String sql = "SELECT DISTINCT t.id FROM Turma t " +
                "JOIN Turma_Participantes tp ON t.id = tp.id_turma " +
                "JOIN Monitor m ON tp.cpf_pessoa = m.cpf " +
                "WHERE m.cpf = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Reutiliza o obterTurmaPorId para carregar a turma completa
                    obterTurmaPorId(rs.getInt("id")).ifPresent(turmas::add);
                }
            }
        }
        return turmas;
    }

    public static List<Turma> listarTurmasPorProfessor(String cpfProfessor) throws SQLException {
        List<Turma> turmas = new ArrayList<>();

        String sql = "SELECT t.id FROM Turma t " +
                "JOIN Turma_Participantes tp ON t.id = tp.id_turma " +
                "WHERE tp.cpf_pessoa = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfProfessor);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    obterTurmaPorId(rs.getInt("id")).ifPresent(turmas::add);
                }
            }
        }
        return turmas;
    }

    public static List<Turma> listarTurmasPorAluno(String cpfAluno) throws SQLException {
        List<Turma> turmas = new ArrayList<>();
        String sql = "SELECT t.id FROM Turma t " +
                "JOIN Turma_Participantes tp ON t.id = tp.id_turma " +
                "WHERE tp.cpf_pessoa = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfAluno);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    obterTurmaPorId(rs.getInt("id")).ifPresent(turmas::add);
                }
            }
        }
        return turmas;
    }
}
