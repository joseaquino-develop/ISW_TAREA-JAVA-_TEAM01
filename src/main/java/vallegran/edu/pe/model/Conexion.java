package vallegran.edu.pe.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Crea conexiones JDBC a la base chifa_fusion_db (la misma que usa la web). */
public final class Conexion {

    private Conexion() {
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + Config.dbHost() + ":" + Config.dbPort() + "/" + Config.dbName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima"
                + "&useUnicode=true&characterEncoding=UTF-8";
        DriverManager.setLoginTimeout(5);
        return DriverManager.getConnection(url, Config.dbUser(), Config.dbPassword());
    }
}
