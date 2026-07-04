package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

    private static final String URL = "jdbc:mysql://localhost:3306/ritual_de_sangue?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
