package vallegran.edu.pe.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lee la configuración desde config.properties (raíz del proyecto).
 * Si el archivo no existe, usa valores por defecto para MySQL local.
 */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        Path archivo = Path.of("config.properties");
        if (Files.exists(archivo)) {
            try (InputStream in = Files.newInputStream(archivo);
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                PROPS.load(reader);
            } catch (IOException e) {
                System.err.println("No se pudo leer config.properties: " + e.getMessage());
            }
        }
    }

    private Config() {
    }

    public static String get(String clave, String porDefecto) {
        String valor = PROPS.getProperty(clave);
        return (valor == null || valor.isBlank()) ? porDefecto : valor.trim();
    }

    public static String dbHost() {
        return get("db.host", "localhost");
    }

    public static String dbPort() {
        return get("db.port", "3306");
    }

    public static String dbName() {
        return get("db.name", "chifa_fusion_db");
    }

    public static String dbUser() {
        return get("db.user", "root");
    }

    /** La contraseña puede estar vacía, por eso no usa el valor por defecto de get(). */
    public static String dbPassword() {
        String valor = PROPS.getProperty("db.password");
        return valor == null ? "" : valor;
    }

    public static String webUrl() {
        return get("web.url", "http://localhost:5000");
    }
}
