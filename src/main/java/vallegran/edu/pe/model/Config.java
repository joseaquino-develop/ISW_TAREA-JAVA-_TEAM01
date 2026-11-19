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

    /** Directorio donde se encuentra config.properties (raíz del proyecto). */
    private static final Path BASE_DIR;

    static {
        Path archivo = Path.of("config.properties").toAbsolutePath().normalize();
        BASE_DIR = archivo.getParent();
        boolean cargado = false;
        if (Files.exists(archivo)) {
            try (InputStream in = Files.newInputStream(archivo);
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                PROPS.load(reader);
                cargado = true;
            } catch (IOException e) {
                System.err.println("No se pudo leer config.properties en " + archivo + ": " + e.getMessage());
            }
        }
        if (cargado) {
            System.out.println("[Config] config.properties cargado desde: " + archivo);
        } else {
            System.err.println("[Config] ADVERTENCIA: no se encontró config.properties en " + archivo
                    + ". Copia config.properties.example y ajusta tus datos; de lo contrario se usarán valores por defecto.");
        }
    }

    /**
     * Resuelve una ruta de archivo: si es absoluta la devuelve tal cual;
     * si es relativa, la resuelve contra el directorio de config.properties
     * (raíz del proyecto), independientemente del working directory de ejecución.
     */
    public static Path resolvePath(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        Path p = Path.of(ruta);
        if (p.isAbsolute()) {
            return p.normalize();
        }
        return BASE_DIR.resolve(p).normalize();
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

    /* ===================== Parámetros del túnel SSH (JSch) ===================== */

    public static String sshHost() {
        return get("ssh.host", "");
    }

    public static int sshPort() {
        return Integer.parseInt(get("ssh.port", "22"));
    }

    public static String sshUser() {
        return get("ssh.user", "");
    }

    public static String sshKey() {
        String valor = PROPS.getProperty("ssh.key");
        if (valor == null || valor.isBlank()) {
            return "";
        }
        Path resuelto = resolvePath(valor);
        return resuelto == null ? "" : resuelto.toString();
    }

    public static Path sshKeyPath() {
        String valor = PROPS.getProperty("ssh.key");
        return resolvePath(valor);
    }

    public static String sshRemoteHost() {
        return get("ssh.remote.host", "database-1.cnqjpfxoqmvf.us-east-1.rds.amazonaws.com");
    }

    public static int sshRemotePort() {
        return Integer.parseInt(get("ssh.remote.port", "3306"));
    }
}
