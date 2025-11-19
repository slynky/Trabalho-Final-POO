package org.teiacoltec.poo.tpf.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/sistema_escolar";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
