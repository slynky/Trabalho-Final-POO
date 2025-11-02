package org.teiacoltec.poo.tpf.conexao.dao;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
        String sqlParticipantes = "SELECT cpf_pessoa FROM Turma_Participantes WHERE id_turma = ?";
        try (Connection conn = ConexaoBD.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlTurma);
        PreparedStatement stmt2 = conn.prepareStatement(sqlParticipantes)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery();
            ResultSet rs2 = stmt2.executeQuery()) {
                if (rs.next()) {
                    String dataInicio = rs.getDate("data_inicio").toLocalDate().format(FORMATADOR);
                    String dataFim = rs.getDate("data_fim").toLocalDate().format(FORMATADOR);

                    if (rs2.next()) {
                        Array sqlArray = rs2.getArray("cpf_pessoa");

                        String[] cpfArray = null;

                        if (sqlArray != null) {
                            try {
                                cpfArray = (String[]) sqlArray.getArray();
                            } finally {
                                sqlArray.free();
                            }
                        }

                        if (cpfArray != null) {
                            Arrays.stream(cpfArray).forEach(cpf -> {

                            });
                        }
                    }

                    Turma turma = new Turma(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            dataInicio,
                            dataFim,
                            null,

                            );

                    return Optional.of(turma);
                }
                return Optional.empty();
            }
        }

    }

}
