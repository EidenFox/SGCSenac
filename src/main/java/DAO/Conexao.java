package DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {

   public static Connection conectar() {
        Properties props = new Properties();


        // IMPORTANTE
        // adicionar um arquivo chamado "db.properties" no diretório "src/main/resources" com as informações do banco.
        // O arquivo deve conter as seguintes linhas:

        // db.url=jdbc:mysql://localhost:<Porta-Do-Banco>/<Nome-Do-Banco>           // porta padrão: 3306
        // db.user=<Usuario>
        // db.password=<Senha>


        try (InputStream input = Conexao.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo db.properties não encontrado em src/main/resources/");
            }

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            Connection conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(true);
            return conn;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar o arquivo db.properties.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados", e);
        }
    }
}