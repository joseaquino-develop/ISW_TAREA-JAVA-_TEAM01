package vallegran.edu.pe.model;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abre y mantiene un túnel SSH local (port forwarding) con JSch hacia la base
 * de datos remota (RDS privada) a través de la instancia EC2 bastión.
 *
 * Flujo:
 *   localhost:{db.port}  -->  [túnel SSH]  -->  {ssh.remote.host}:{ssh.remote.port}
 *
 * La sesión JSch se autentica con la llave privada .pem configurada en
 * config.properties (ssh.key). El túnel se cierra automáticamente al apagar la
 * JVM mediante un shutdown hook, y también puede cerrarse explícitamente con
 * stop().
 *
 * <p>Se usa {@code com.github.mwiede:jsch} (mantenedor activo de JSch) porque
 * soporta los algoritmos modernos de claves públicas exigidos por Ubuntu 24.04
 * (rsa-sha2-256, rsa-sha2-512, ssh-ed25519) que la versión antigua
 * {@code com.jcraft:jsch 0.1.55} no soporta, evitando el error "Auth fail".
 */
public final class SshTunnel {

    private static final SshTunnel INSTANCE = new SshTunnel();

    private static final boolean DEBUG =
            Boolean.parseBoolean(System.getProperty("ssh.tunnel.debug", "false"));

    private Session session;
    private volatile boolean iniciado = false;

    private SshTunnel() {
    }

    public static SshTunnel getInstance() {
        return INSTANCE;
    }

    /**
     * Inicia la sesión JSch (llave privada .pem) y abre el reenvío de puertos
     * local hacia la RDS privada. Si ya hay una sesión activa, no hace nada.
     *
     * @throws SshTunnelException si faltan parámetros o falla la conexión/túnel SSH.
     */
    public synchronized void start() throws SshTunnelException {
        if (iniciado && session != null && session.isConnected()) {
            return;
        }

        String host = Config.sshHost();
        int puerto = Config.sshPort();
        String usuario = Config.sshUser();
        Path llave = Config.sshKeyPath();
        String remotoHost = Config.sshRemoteHost();
        int remotoPuerto = Config.sshRemotePort();
        int puertoLocal = Integer.parseInt(Config.dbPort());

        if (host.isBlank() || usuario.isBlank() || llave == null) {
            throw new SshTunnelException(
                    "Faltan parámetros SSH en config.properties (ssh.host, ssh.user, ssh.key). "
                            + "Revisa el archivo y copia config.properties.example si es necesario.");
        }

        if (!Files.isReadable(llave)) {
            throw new SshTunnelException(
                    "No se puede leer la llave privada SSH en: " + llave.toAbsolutePath()
                            + ". Verifica que la ruta en ssh.key sea correcta y que el archivo exista. "
                            + "Puedes usar una ruta absoluta (ej. C:\\llaves\\miviaje.pem) o una ruta relativa "
                            + "desde la raíz del proyecto (ej. keywp (4).pem).");
        }

        try {
            JSch jsch = new JSch();

            // Logger opcional de JSch para depurar la negociación SSH (activar con
            // -Dssh.tunnel.debug=true al ejecutar).
            if (DEBUG) {
                jsch.setLogger(new JSchLogger());
                System.out.println("[SSH] Modo debug de JSch activado.");
            }

            // Llave privada .pem (autenticación por clave pública, sin contraseña)
            jsch.addIdentity(llave.toString());

            session = jsch.getSession(usuario, host, puerto);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "publickey");
            // Algoritmos de clave pública soportados por Ubuntu 24.04/OpenSSH moderno.
            session.setConfig("PubkeyAcceptedAlgorithms",
                    "ssh-rsa,rsa-sha2-256,rsa-sha2-512,ssh-ed25519");
            // Algoritmos de clave de host del servidor.
            session.setConfig("server_host_key",
                    "ssh-rsa,rsa-sha2-256,rsa-sha2-512,ssh-ed25519,ecdsa-sha2-nistp256");
            session.connect(15000); // timeout de conexión: 15 s

            // Reenvío de puertos: localhost:puertoLocal -> remotoHost:remotoPuerto
            session.setPortForwardingL(puertoLocal, remotoHost, remotoPuerto);

            iniciado = true;
            System.out.println("[SSH] Túnel abierto: localhost:" + puertoLocal + " -> "
                    + remotoHost + ":" + remotoPuerto + " vía " + host + ":" + puerto);
        } catch (JSchException e) {
            iniciado = false;
            if (session != null) {
                session.disconnect();
                session = null;
            }
            throw new SshTunnelException(
                    "No se pudo establecer el túnel SSH hacia la BD remota. "
                            + "Causa JSch: " + e.getMessage()
                            + (DEBUG ? ". Revisa los mensajes de depuración de JSch arriba." : ""),
                    e);
        }
    }

    /**
     * Cierra la sesión JSch y libera el puerto de reenvío local. Idempotente.
     */
    public synchronized void stop() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        session = null;
        iniciado = false;
        System.out.println("[SSH] Túnel cerrado.");
    }

    public boolean estaActivo() {
        return session != null && session.isConnected();
    }

    /**
     * Registra un shutdown hook para que la sesión SSH y el túnel se cierren
     * al apagar o salir de la aplicación.
     */
    public static void registrarCierreAlApagar() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(SshTunnel.getInstance()::stop, "ssh-tunnel-shutdown-hook"));
    }

    /**
     * Logger mínimo de JSch que imprime los mensajes de negociación SSH a consola
     * cuando el modo debug está activado (-Dssh.tunnel.debug=true).
     */
    private static class JSchLogger implements Logger {

        private static final boolean DEBUG =
                Boolean.parseBoolean(System.getProperty("ssh.tunnel.debug", "false"));

        @Override
        public boolean isEnabled(int level) {
            return DEBUG;
        }

        @Override
        public void log(int level, String message) {
            if (!DEBUG) {
                return;
            }
            String nivel;
            if (level == com.jcraft.jsch.Logger.DEBUG) {
                nivel = "DEBUG";
            } else if (level == com.jcraft.jsch.Logger.INFO) {
                nivel = "INFO ";
            } else if (level == com.jcraft.jsch.Logger.WARN) {
                nivel = "WARN ";
            } else if (level == com.jcraft.jsch.Logger.ERROR) {
                nivel = "ERROR";
            } else if (level == com.jcraft.jsch.Logger.FATAL) {
                nivel = "FATAL";
            } else {
                nivel = "?????";
            }
            System.out.println("[JSch " + nivel + "] " + message);
        }
    }
}
