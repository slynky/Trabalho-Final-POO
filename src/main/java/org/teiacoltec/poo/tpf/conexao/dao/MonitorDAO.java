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

    public static void inserirEspecifico(Connection conn, Monitor monitor) throws SQLException {
        String sqlMonitor = "INSERT INTO Monitor (cpf, matricula, curso) VALUES (?, ?, ?)";

        try (PreparedStatement stmtMonitor = conn.prepareStatement(sqlMonitor)) {
            stmtMonitor.setString(1, monitor.getCpf());
            stmtMonitor.setString(2, monitor.getMatricula());
            stmtMonitor.setString(3, monitor.getCurso());
            stmtMonitor.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public static Optional<Monitor> buscarPorCpf(String cpf, String senha) throws SQLException {
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "m.matricula, m.curso " +
                "FROM Pessoa p " +
                "JOIN Monitor m ON p.cpf = m.cpf " +
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
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw e;
        }

    }

    public static List<Monitor> listarTodosMonitoresDaTurma(int idTurma) throws SQLException {
        List<Monitor> monitores = new ArrayList<>();

        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "m.matricula, m.curso " +
                "FROM Pessoa p " +
                "JOIN Monitor m ON p.cpf = m.cpf " +
                "JOIN Turma_Participantes tp ON tp.cpf_pessoa = p.cpf " +
                "WHERE tp.id_turma = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTurma);

            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return monitores;
    }

    public static void removerMonitor(String cpf,Connection conn) throws SQLException {
        String sqlMonitor = "DELETE FROM Monitor WHERE cpf = ?";
        try (PreparedStatement stmtMonitor = conn.prepareStatement(sqlMonitor)) {
            stmtMonitor.setString(1, cpf);
            stmtMonitor.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}