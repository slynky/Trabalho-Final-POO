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

public class TarefaDAO {
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void inserirTarefa(Tarefa tarefa) throws SQLException {

        String sqlTarefa = "INSERT INTO Tarefa (nome, id_turma, id_atividade, nota) " + "VALUES ( ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlTarefa)) {


                stmt.setString(1, tarefa.getNome());
                stmt.setInt(2, tarefa.getTurmaId());
                stmt.setInt(3, tarefa.getAtividadeId());
                stmt.setFloat(4, tarefa.getNota());


                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {conn.commit();}



            }catch (SQLException e){
                System.out.println("Erro ao inserir tarefa. Invertendo transacao.");
                conn.rollback();
                throw e;
            }
        }

    }

    public static Optional<Tarefa> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Tarefa WHERE id = ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    int idParaConstrutor  = rs.getInt("id");
                    String nomeParaConstrutor = rs.getString("nome");
                    int turmaIdParaConstrutor = rs.getInt("turmaId");
                    int atividadeIdParaConstrutor = rs.getInt("atividadeId");
                    float notaParaConstrutor = rs.getFloat("nota");
                    Turma turma = TurmaDAO.obterTurmaPorId(turmaIdParaConstrutor).get();
                    Atividade atividade = AtividadeDAO.buscarPorId(atividadeIdParaConstrutor).get();
                    Tarefa tarefa = new Tarefa(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            turma,
                            atividade,
                            rs.getFloat("nota")
                    );
                    return Optional.of(tarefa);
                }
                return null;
            }
        }
    }


}
