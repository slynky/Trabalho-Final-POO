package org.teiacoltec.poo.tpf.conexao.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.util.Criptografar;

public class ProfessorDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void inserir(Professor professor) throws SQLException {

        String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, nascimento, email, endereco, senha, tipo_pessoa) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'PROFESSOR')";

        String sqlProfessor = "INSERT INTO Professor (cpf, matricula, formacao) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa);
                 PreparedStatement stmtProfessor = conn.prepareStatement(sqlProfessor)) {

                stmtPessoa.setString(1, professor.getCpf());
                stmtPessoa.setString(2, professor.getNome());
                stmtPessoa.setDate(3, Date.valueOf(professor.getNascimento()));
                stmtPessoa.setString(4, professor.getEmail());
                stmtPessoa.setString(5, professor.getEndereco());
                stmtPessoa.setString(6, professor.getSenha());

                stmtProfessor.setString(1, professor.getCpf());
                stmtProfessor.setString(2, professor.getMatricula());
                stmtProfessor.setString(3, professor.getFormacao());

                stmtPessoa.executeUpdate();
                stmtProfessor.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                System.err.println("Erro ao inserir professor. Revertendo transação.");
                conn.rollback();
                throw e;
            }
        }
    }

    public static Optional<Professor> buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "pr.matricula, pr.formacao " +
                "FROM Pessoa p " +
                "JOIN Professor pr ON p.cpf = pr.cpf " +
                "WHERE p.cpf = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dataNasc = rs.getDate("nascimento").toLocalDate().format(FORMATADOR);

                    Professor professor = new Professor(
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            dataNasc,
                            rs.getString("email"),
                            rs.getString("endereco"),
                            rs.getString("matricula"),
                            rs.getString("formacao"),
                            null
                    );
                    return Optional.of(professor);
                }
            }
        }
        return Optional.empty();
    }

    public static List<Professor> listarTodos() throws SQLException {
        List<Professor> professores = new ArrayList<>();
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "pr.matricula, pr.formacao " +
                "FROM Pessoa p " +
                "JOIN Professor pr ON p.cpf = pr.cpf";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dataNasc = rs.getDate("nascimento").toLocalDate().format(FORMATADOR);

                Professor professor = new Professor(
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        dataNasc,
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("matricula"),
                        rs.getString("formacao"),
                        null
                );
                professores.add(professor);
            }
        }
        return professores;
    }

    public static void atualizarEndereco(String cpf, String novoEndereco) throws SQLException {
        String sql = "UPDATE Pessoa SET endereco = ? WHERE cpf = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoEndereco);
            stmt.setString(2, cpf);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhum aluno com o CPF informado foi encontrado.");
            }
        }
    }
}
