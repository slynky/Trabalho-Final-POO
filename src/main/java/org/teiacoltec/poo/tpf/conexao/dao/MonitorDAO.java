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
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Monitor;
import org.teiacoltec.poo.tpf.util.Criptografar;

public class MonitorDAO {
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static void inserir(Monitor monitor) throws SQLException {
        String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, nascimento, email, endereco, senha, tipo_pessoa) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'MONITOR')";

        String sqlMonitor = "INSERT INTO Monitor (cpf, matricula, curso) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa);
                 PreparedStatement stmtMonitor = conn.prepareStatement(sqlMonitor)) {
                stmtPessoa.setString(1, monitor.getCpf());
                stmtPessoa.setString(2, monitor.getNome());
                stmtPessoa.setDate(3, Date.valueOf(monitor.getNascimento()));
                stmtPessoa.setString(4, monitor.getEmail());
                stmtPessoa.setString(5, monitor.getEndereco());
                stmtPessoa.setString(6, monitor.getSenha());

                stmtMonitor.setString(1, monitor.getCpf());
                stmtMonitor.setString(2, monitor.getMatricula());
                stmtMonitor.setString(3, monitor.getCurso());

                stmtPessoa.executeUpdate();
                stmtMonitor.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                System.err.println("Erro ao inserir monitor. Revertendo transação.");
                conn.rollback();
                throw e;
            }
        }
    }

    public static Optional<Monitor> buscarPorCpf(String cpf, String senha) throws SQLException {
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "m.matricula, m.curso " +
                "FROM Pessoa p " +
                "JOIN MONITOR m ON m.cpf = m.cpf " +
                "WHERE p.cpf = ? AND p.tipo_pessoa = 'MONITOR' AND p.senha = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            senha = Criptografar.hashSenhaMD5(senha);
            stmt.setString(1, cpf);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    String dataNasc = rs.getDate("nascimento").toLocalDate().format(FORMATADOR);

                    Monitor monitor = new Monitor(
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            dataNasc,
                            rs.getString("email"),
                            rs.getString("endereco"),
                            rs.getString("matricula"),
                            rs.getString("curso"),
                            null
                    );
                    return Optional.of(monitor);
                }

            }
        }
        return Optional.empty();
    }

    public static List<Monitor> listarTodos() throws SQLException {
        List<Monitor> monitores = new ArrayList<>();
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "m.matricula, m.curso " +
                "FROM Pessoa p " +
                "JOIN Monitor m ON p.cpf = m.cpf";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dataNasc = rs.getDate("nascimento").toLocalDate().format(FORMATADOR);

                Monitor monitor = new Monitor(
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        dataNasc,
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("matricula"),
                        rs.getString("curso"),
                        null
                );
                monitores.add(monitor);
            }
        }
        return monitores;
    }
}
