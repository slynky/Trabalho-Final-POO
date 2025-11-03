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

public class AtividadeDAO {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");



    public static void insetirAtividade(Atividade atividade) throws SQLException {

        String sqlAtividade = "INSERT INTO Atividade (nome, descricao, data_inicio, data_fim, valor) " + "VALUES ( ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()){
           conn.setAutoCommit(false);
           try (PreparedStatement stmt = conn.prepareStatement(sqlAtividade)) {

               stmt.setString(1, atividade.getNome());
               stmt.setString(2, atividade.getDesc());
               stmt.setDate(3, Date.valueOf(atividade.getInicio()));
               stmt.setDate(4, Date.valueOf(atividade.getFim()));
               stmt.setFloat(5, atividade.getValor());

               int linhasAfetadas = stmt.executeUpdate();
               if (linhasAfetadas > 0) {conn.commit();}

           }catch (SQLException e){
               System.err.println("Erro ao inserir atividade. Revertendo transacao");
               conn.rollback();
               throw e;
           }
        }
    }

    public static Optional<Atividade> buscarPorId(int id) throws SQLException{
        String sqlAtividade = "SELECT * FROM Atividade WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlAtividade)){

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    String dataInicio = rs.getDate("data_inicio").toLocalDate().format(FORMATADOR);
                    String dataFim = rs.getDate("data_fim").toLocalDate().format(FORMATADOR);

                    Atividade atividade = new Atividade(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            dataInicio,
                            dataFim,
                            rs.getFloat("valor")
                    );
                    return Optional.of(atividade);
                }
            }
        }

        return Optional.empty();
    }

    public static void removerAtividade(int id) throws SQLException {
        String sqlAtividade = "DELETE FROM Atividade WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmtAtividade = conn.prepareStatement(sqlAtividade)) {

            stmtAtividade.setInt(1, id);
            stmtAtividade.executeUpdate();
        }
    }

}
