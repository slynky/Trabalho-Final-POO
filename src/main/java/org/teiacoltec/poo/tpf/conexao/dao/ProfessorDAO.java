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

public class ProfessorDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void inserirEspecifico(Connection conn, Professor professor) throws SQLException {
        String sqlProfessor = "INSERT INTO Professor (cpf, matricula, formacao) VALUES (?, ?, ?)";

        try (PreparedStatement stmtProfessor = conn.prepareStatement(sqlProfessor)) {
            stmtProfessor.setString(1, professor.getCpf());
            stmtProfessor.setString(2, professor.getMatricula());
            stmtProfessor.setString(3, professor.getFormacao());
            stmtProfessor.executeUpdate();
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

    public static List<Professor> listarTodosProfessoresDaTurma(int idTurma) throws SQLException {
        List<Professor> professores = new ArrayList<>();

        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "pr.matricula, pr.formacao " +
                "FROM Pessoa p " +
                "JOIN Professor pr ON p.cpf = pr.cpf " +
                "JOIN Turma_Participantes tp ON tp.cpf_pessoa = p.cpf " +
                "WHERE tp.id_turma = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTurma);

            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return professores;
    }
}