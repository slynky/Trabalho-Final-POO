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

    public static void inserir(Pessoa pessoa) throws SQLException {
        String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, nascimento, email, endereco, senha, tipo_pessoa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa)) {

                stmtPessoa.setString(1, pessoa.getCpf());
                stmtPessoa.setString(2, pessoa.getNome());
                stmtPessoa.setDate(3, Date.valueOf(pessoa.getNascimento()));
                stmtPessoa.setString(4, pessoa.getEmail());
                stmtPessoa.setString(5, pessoa.getEndereco());
                stmtPessoa.setString(6, pessoa.getSenha());

                // Define o tipo e executa a inserção na tabela Pessoa e na tabela específica
                if (pessoa instanceof Aluno aluno) {
                    stmtPessoa.setString(7, "ALUNO");
                    stmtPessoa.executeUpdate();
                    AlunoDAO.inserirEspecifico(conn, aluno);
                } else if (pessoa instanceof Professor professor) {
                    stmtPessoa.setString(7, "PROFESSOR");
                    stmtPessoa.executeUpdate();
                    ProfessorDAO.inserirEspecifico(conn, professor);
                } else if (pessoa instanceof Monitor monitor) {
                    stmtPessoa.setString(7, "MONITOR");
                    stmtPessoa.executeUpdate();
                    MonitorDAO.inserirEspecifico(conn, monitor);
                }

                conn.commit();

            } catch (SQLIntegrityConstraintViolationException e) {
                // Lógica de UPSERT: Se for duplicação de chave primária, chama a atualização
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("PRIMARY")) {
                    conn.rollback();
                    System.out.println("Pessoa com CPF " + pessoa.getCpf() + " já existe. Chamando método de atualização.");
                    atualizar(pessoa);
                    return;
                }
                // Se for outro erro de integridade, propaga
                conn.rollback();
                System.err.println("Erro ao inserir pessoa. Revertendo transação.");
                throw e;

            } catch (SQLException e) {
                // Tratamento de erro geral de SQL
                System.err.println("Erro ao inserir pessoa. Revertendo transação.");
                if (conn != null) conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                // Garante que o estado de autocommit seja restaurado e a conexão fechada
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static void atualizar(Pessoa pessoa) throws SQLException {
        String sqlPessoa = "UPDATE Pessoa SET nome = ?, nascimento = ?, email = ?, endereco = ?, senha = ?, tipo_pessoa = ? WHERE cpf = ?";

        Connection conn = null; // Aberta fora do try-with-resources para uso no finally
        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa)) {

                stmtPessoa.setString(1, pessoa.getNome());
                stmtPessoa.setDate(2, Date.valueOf(pessoa.getNascimento()));
                stmtPessoa.setString(3, pessoa.getEmail());
                stmtPessoa.setString(4, pessoa.getEndereco());
                stmtPessoa.setString(5, pessoa.getSenha()); // Assumindo que a senha já está hasheada

                // Define o tipo e executa a atualização
                if (pessoa instanceof Aluno aluno) {
                    stmtPessoa.setString(6, "ALUNO");
                    stmtPessoa.setString(7, aluno.getCpf());
                    stmtPessoa.executeUpdate();
                    AlunoDAO.atualizarEspecifico(conn, aluno);
                } else if (pessoa instanceof Professor professor) {
                    stmtPessoa.setString(6, "PROFESSOR");
                    stmtPessoa.setString(7, professor.getCpf());
                    stmtPessoa.executeUpdate();
                    ProfessorDAO.atualizarEspecifico(conn, professor);
                } else if (pessoa instanceof Monitor monitor) {
                    stmtPessoa.setString(6, "MONITOR");
                    stmtPessoa.setString(7, monitor.getCpf());
                    stmtPessoa.executeUpdate();
                    MonitorDAO.atualizarEspecifico(conn, monitor);
                }

                conn.commit();

            } catch (SQLException e) {
                System.err.println("Erro ao atualizar pessoa. Revertendo transação. Erro: " + e.getMessage());
                if (conn != null) conn.rollback(); // Garantia de rollback
                throw e; // Propaga a exceção para o chamador (TurmaDAO)
            }
        } finally {
            if (conn != null) {
                // Garante que o estado seja restaurado e a conexão seja fechada
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static Optional<Pessoa> buscarPessoaPorCpfSemValidacao(String cpf) throws SQLException {
        String sqlTipo = "SELECT tipo_pessoa FROM Pessoa WHERE cpf = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlTipo)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo_pessoa");

                    // Chama a sobrecarga do DAO específico que não requer senha
                    switch (tipo) {
                        case "ALUNO":
                            return AlunoDAO.buscarPorCpf(cpf).map(a -> (Pessoa)a);
                        case "PROFESSOR":
                            return ProfessorDAO.buscarPorCpf(cpf).map(p -> (Pessoa)p);
                        case "MONITOR":
                            return MonitorDAO.buscarPorCpf(cpf).map(m -> (Pessoa)m);
                    }
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw e;
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
                            return ProfessorDAO.buscarPorCpf(cpf, senha).map(p -> (Pessoa)p);
                        case "MONITOR":
                            return MonitorDAO.buscarPorCpf(cpf, senha).map(m -> (Pessoa)m);
                    }
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw e;
        }

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

    public static void removerPessoa(String cpf) throws SQLException {

        String sqlTipo = "SELECT tipo_pessoa FROM Pessoa WHERE cpf = ?";
        String sqlPessoa = "DELETE FROM Pessoa WHERE cpf = ?";

        Connection conn = null;

        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            String tipoPessoa = null;

            try (PreparedStatement stmtTipo = conn.prepareStatement(sqlTipo)) {
                stmtTipo.setString(1, cpf);
                try (ResultSet rs = stmtTipo.executeQuery()) {
                    if (rs.next()) {
                        tipoPessoa = rs.getString("tipo_pessoa");
                    } else {
                        throw new SQLException("Pessoa com CPF " + cpf + " não encontrada.");
                    }
                }
            }

            if (tipoPessoa != null) {
                switch (tipoPessoa) {
                    case "ALUNO":
                        AlunoDAO.removerAluno(cpf, conn);
                        break;
                    case "PROFESSOR":
                        ProfessorDAO.removerProfessor(cpf, conn);
                        break;
                    case "MONITOR":
                        MonitorDAO.removerMonitor(cpf, conn);
                        break;
                    default:
                        throw new SQLException("Tipo de pessoa desconhecido: " + tipoPessoa);
                }
            }

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa)) {
                stmtPessoa.setString(1, cpf);
                int linhasAfetadas = stmtPessoa.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao deletar a Pessoa, CPF pode não existir mais.");
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw ex;
                }
            }
            throw new SQLException("Erro ao remover pessoa: " + e.getMessage(), e);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
        }
    }
}