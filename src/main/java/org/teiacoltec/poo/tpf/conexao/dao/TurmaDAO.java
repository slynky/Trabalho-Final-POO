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
import org.teiacoltec.poo.tpf.escolares.Atividade;
import org.teiacoltec.poo.tpf.conexao.dao.AtividadeDAO;
import org.teiacoltec.poo.tpf.escolares.Tarefa;
import org.teiacoltec.poo.tpf.escolares.instituicoesEscolares.Turma;
import org.teiacoltec.poo.tpf.escolares.membrosEscolares.Professor;
import org.teiacoltec.poo.tpf.util.Criptografar;

public class TurmaDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");



    public static void inserirTurma(Turma turma) throws SQLException {


        String sqlTurma = "INSERT INTO Turma (nome, descricao, data_inicio, data_fim, id_turma_pai) " + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stm = conn.prepareStatement(sqlTurma)) {

                stm.setString(1, turma.getNome());
                stm.setString(2, turma.getDesc());
                stm.setDate(3, Date.valueOf(turma.getInicio()));
                stm.setDate(4, Date.valueOf(turma.getFim()));
                if(turma.getTurmaPai() != null) {
                    stm.setInt(5, turma.getTurmaPai().getId());
                }else{
                    stm.setNull(5, java.sql.Types.INTEGER);
                }

                int linhasAfetadas = stm.executeUpdate();
                if (linhasAfetadas > 0) {conn.commit();}

            }catch (SQLException e){
                System.err.println("Erro ao inserir Turma. Revertendo processo. Erro: "+e.getMessage());
                conn.rollback();
            }
        }
    }

    public static Optional<Turma> obterTurmaPorId(int id) throws SQLException {

        String sqlTurma = "SELECT * FROM Turmas WHERE id = ?";
        try (Connection conn = ConexaoBD.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlTurma)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dataInicio = rs.getDate("data_inicio").toLocalDate().format(FORMATADOR);
                    String dataFim = rs.getDate("data_fim").toLocalDate().format(FORMATADOR);

                    Turma turma = new Turma(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            dataInicio,
                            dataFim,
                            null,
                           // rs.getInt("id_turma_pai"));
                            null);
                    return Optional.of(turma);
                }
                return Optional.empty();
            }
        }

    }

}
