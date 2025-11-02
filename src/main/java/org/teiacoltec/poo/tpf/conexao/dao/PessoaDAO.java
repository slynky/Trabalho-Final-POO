package org.teiacoltec.poo.tpf.conexao.dao;

import java.sql.*;
import java.util.Optional;

import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.pessoa.Pessoa;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.util.Criptografar;

public class PessoaDAO {

    public static void inserir(Pessoa pessoa) throws SQLException { //testar com pessoa.getCpf e tals acho q n pode por pessoa ser abstrato
        String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, nascimento, email, endereco, senha, tipo_pessoa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa)) {

                stmtPessoa.setString(1, pessoa.getCpf());
                stmtPessoa.setString(2, pessoa.getNome());
                stmtPessoa.setDate(3, Date.valueOf(pessoa.getNascimento()));
                stmtPessoa.setString(4, pessoa.getEmail());
                stmtPessoa.setString(5, pessoa.getEndereco());
                stmtPessoa.setString(6, pessoa.getSenha());
                if (pessoa instanceof Aluno) {
                    stmtPessoa.setString(7, "ALUNO");
                    stmtPessoa.executeUpdate();
                    AlunoDAO.inserirEspecifico(conn, (Aluno) pessoa);
                } else if (pessoa instanceof Professor) {
                    stmtPessoa.setString(7, "PROFESSOR");
                    stmtPessoa.executeUpdate();
                    ProfessorDAO.inserirEspecifico(conn, (Professor) pessoa);
                } else if (pessoa instanceof Monitor) {
                    stmtPessoa.setString(7, "MONITOR");
                    stmtPessoa.executeUpdate();
                    MonitorDAO.inserirEspecifico(conn, (Monitor) pessoa);
                }

                conn.commit();

            } catch (SQLException e) {
                System.err.println("Erro ao inserir pessoa. Revertendo transação.");
                conn.rollback();
                throw e;
            }
        }
    }

    public static Optional<Pessoa> buscarPessoaPorCpf(String cpf, String senha) throws SQLException {
        String sqlTipo = "SELECT tipo_pessoa FROM Pessoa WHERE cpf = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlTipo)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo_pessoa");

                    switch (tipo) {
                        case "ALUNO":
                            return AlunoDAO.buscarPorCpf(cpf, senha).map(a -> (Pessoa)a);

                        case "PROFESSOR":
                            return ProfessorDAO.buscarPorCpf(cpf).map(p -> (Pessoa)p);

                        case "MONITOR":
                            return MonitorDAO.buscarPorCpf(cpf, senha).map(m -> (Pessoa)m);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public static void atualizarEndereco(String cpf, String novoEndereco, String senha) throws SQLException {
        senha = Criptografar.hashSenhaMD5(senha);
        String sql = "UPDATE Pessoa SET endereco = ? WHERE cpf = ? AND senha = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoEndereco);
            stmt.setString(2, cpf);
            stmt.setString(3, senha);

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Senha incorreta. O endereço não foi atualizado.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}