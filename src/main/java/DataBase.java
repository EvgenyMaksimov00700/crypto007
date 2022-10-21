import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBase {
    private String DbURL;
    private String user;
    private String password;
    private Connection connection;

    /**
     * Конструктор для подключения к базе данных с полями сетевого адреса, логина и пароля
     * @param nameDB название базы данных
     */
    @SneakyThrows
    public DataBase(String nameDB) {
        Class.forName("org.postgresql.Driver");
        DbURL = "jdbc:postgresql://localhost:5432/" + nameDB;
        password = "ProgressRX580";
        user = "postgres";

        connection = DriverManager.getConnection(DbURL, user, password);
    }

    /**
     * Получаем собственное подключение к базе данных
     * @return подключение
     */
    public Connection getConnection() {
        return connection;
    }
}


