import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBase {
    private String DbURL;
    private String user;
    private String password;
    private Connection connection;


    public Connection getConnection() {
        return connection;
    }

    @SneakyThrows
    public DataBase(String nameDB) {
        Class.forName("org.postgresql.Driver");
        DbURL = "jdbc:postgresql://localhost:5432/" + nameDB;
        password = "ProgressRX580";
        user = "postgres";

        connection = DriverManager.getConnection(DbURL, user, password);
    }

}


