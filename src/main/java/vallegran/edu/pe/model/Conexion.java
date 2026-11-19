package vallegran.edu.pe.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Crea conexiones JDBC a la base chifa_fusion_db a través del túnel SSH local.
 *
 * Antes de realizar la conexión JDBC, el código lee la configuración, inicia la
 * sesión JSch con la llave privada (.pem) y abre el reenvío de puertos SSH
 * (port forwarding) localmente en el puerto 3306 hacia el RDS privado
 * (database-1.cnqjpfxoqmvf.us-east-1.rds.amazonaws.com:3306) a través de la
 * instancia EC2 bastión configurada en config.properties.
 *
 * La sesión SSH y el túnel se cierran al apagar la aplicación mediante un
 * shutdown hook (registrado en SshTunnel) y también al cerrar la ventana
 * principal (Main).
 */
public final class Conexion {

    private Conexion() {
    }

    static {
        // Asegura el cierre del túnel SSH cuando la JVM se apaga.
        SshTunnel.registrarCierreAlApagar();
    }

    public static Connection getConnection() throws SQLException {
        // 1. Leer configuración y abrir el túnel SSH antes del JDBC.
        try {
            SshTunnel.getInstance().start();
        } catch (SshTunnelException e) {
            throw new SQLException(
                    "No se pudo iniciar el túnel SSH hacia la base de datos remota. "
                            + "Revisa config.properties y la llave .pem.\nDetalle: " + e.getMessage(),
                    e);
        }

        // 2. Conexión JDBC contra localhost (el túnel reenvía al RDS privado).
        String url = "jdbc:mysql://" + Config.dbHost() + ":" + Config.dbPort() + "/" + Config.dbName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima"
                + "&useUnicode=true&characterEncoding=UTF-8";
        DriverManager.setLoginTimeout(5);
        return DriverManager.getConnection(url, Config.dbUser(), Config.dbPassword());
    }
}
