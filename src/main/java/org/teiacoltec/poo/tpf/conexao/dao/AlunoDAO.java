package org.teiacoltec.poo.tpf.conexao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.teiacoltec.poo.tpf.conexao.ConexaoBD;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Aluno;
import org.teiacoltec.poo.tpf.util.Criptografar;

public class AlunoDAO {
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void inserirEspecifico(Connection conn, Aluno aluno) throws SQLException {
        String sqlAluno = "INSERT INTO Aluno (cpf, matricula, curso) VALUES (?, ?, ?)";

        try (PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno)) {
            stmtAluno.setString(1, aluno.getCpf());
            stmtAluno.setString(2, aluno.getMatricula());
            stmtAluno.setString(3, aluno.getCurso());
            stmtAluno.executeUpdate();
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

    public static List<Aluno> listarTodosAlunosDaTurma(int idTurma) throws SQLException {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT p.cpf, p.nome, p.nascimento, p.email, p.endereco, " +
                "a.matricula, a.curso " +
                "FROM Pessoa p " +
                "JOIN Aluno a ON p.cpf = a.cpf " +
                "JOIN Turma_Participantes tp ON tp.cpf_pessoa = p.cpf AND tp.id_turma = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,idTurma);

            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return alunos;
    }
}