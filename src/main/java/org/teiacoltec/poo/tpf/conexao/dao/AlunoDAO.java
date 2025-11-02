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
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.util.Criptografar;

public class AlunoDAO {
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void inserir(Aluno aluno) throws SQLException {
        String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, nascimento, email, endereco, senha, tipo_pessoa) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'ALUNO')";

        String sqlAluno = "INSERT INTO Aluno (cpf, matricula, curso) VALUES (?, ?, ?)";

        // Obtém a conexão
        try (Connection conn = ConexaoBD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa);
                 PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno)) {
                stmtPessoa.setString(1, aluno.getCpf());
                stmtPessoa.setString(2, aluno.getNome());
                stmtPessoa.setDate(3, Date.valueOf(aluno.getNascimento()));
                stmtPessoa.setString(4, aluno.getEmail());
                stmtPessoa.setString(5, aluno.getEndereco());
                stmtPessoa.setString(6, aluno.getSenha());

                stmtAluno.setString(1, aluno.getCpf());
                stmtAluno.setString(2, aluno.getMatricula());
                stmtAluno.setString(3, aluno.getCurso());

                stmtPessoa.executeUpdate();
                stmtAluno.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                System.err.println("Erro ao inserir aluno. Revertendo transação.");
                conn.rollback();
                throw e;
            }
        }
    }

    public static Optional<Aluno> buscarPorCpf(String cpf, String senha) throws SQLException {
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "a.matricula, a.curso " +
                "FROM Pessoa p " +
                "JOIN Aluno a ON p.cpf = a.cpf " +
                "WHERE p.cpf = ? AND p.tipo_pessoa = 'ALUNO' AND p.senha = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            senha = Criptografar.hashSenhaMD5(senha);
            stmt.setString(1, cpf);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    String dataNasc = rs.getDate("nascimento").toLocalDate().format(FORMATADOR);

                    Aluno aluno = new Aluno(
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            dataNasc,
                            rs.getString("email"),
                            rs.getString("endereco"),
                            rs.getString("matricula"),
                            rs.getString("curso"),
                            null
                    );
                    return Optional.of(aluno);
                }

            }
        }
        return Optional.empty();
    }

    public static List<Aluno> listarTodos() throws SQLException {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "a.matricula, a.curso " +
                "FROM Pessoa p " +
                "JOIN Aluno a ON p.cpf = a.cpf";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dataNasc = rs.getDate("nascimento").toLocalDate().format(FORMATADOR);

                Aluno aluno = new Aluno(
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        dataNasc,
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("matricula"),
                        rs.getString("curso"),
                        null
                );
                alunos.add(aluno);
            }
        }
        return alunos;
    }

    public static void atualizarEndereco(String cpf, String novoEndereco, String senha) throws SQLException {
        // Criptografa a senha fornecida para comparação no banco
        senha = Criptografar.hashSenhaMD5(senha);

        String sql = "UPDATE Pessoa SET endereco = ? WHERE cpf = ? AND senha = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoEndereco);
            stmt.setString(2, cpf);
            stmt.setString(3, senha);

            int linhasAfetadas = stmt.executeUpdate();

            // Se nenhuma linha foi atualizada, e assumindo que o CPF existe, a senha estava incorreta.
            if (linhasAfetadas == 0) {
                // Lança uma exceção informando o erro específico.
                throw new SQLException("Senha incorreta. O endereço não foi atualizado.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
