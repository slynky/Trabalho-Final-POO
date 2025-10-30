import org.teiacoltec.poo.tpf.conexao.ConexaoBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TesteConexao {



    public static void main(String[] args) {

        try (Connection conn = ConexaoBD.getConnection()) {

            System.out.println("Conexão com o banco de dados Docker bem-sucedida!");

            try (Statement stmt = conn.createStatement()) {

                String sqlInsert = "INSERT INTO Pessoa (cpf, nome, nascimento, email, endereco, senha, tipo_pessoa) " +
                        "VALUES ('12345678901', 'Prof. Ada Lovelace', '1815-12-10', 'ada@example.com', 'Rua B, 123', 'senha', 'PROFESSOR');";

                String sqlInsertProf = "INSERT INTO Professor (cpf, matricula, formacao) " +
                        "VALUES ('12345678901', 'P98765', 'Ciência da Computação');";

                stmt.executeUpdate(sqlInsert);
                stmt.executeUpdate(sqlInsertProf);


                // Exemplo de consulta
                String sqlQuery = "SELECT p.nome, pr.formacao FROM Pessoa p " +
                        "JOIN Professor pr ON p.cpf = pr.cpf " +
                        "WHERE p.cpf = '12345678901'";

                try (ResultSet rs = stmt.executeQuery(sqlQuery)) {
                    if (rs.next()) {
                        System.out.println("--- Consulta de Teste ---");
                        System.out.println("Professor: " + rs.getString("nome"));
                        System.out.println("Formação: " + rs.getString("formacao"));
                    } else {
                        System.out.println("Professor de teste ainda não inserido.");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao conectar ou executar a query:");
            e.printStackTrace();
        }
    }
}