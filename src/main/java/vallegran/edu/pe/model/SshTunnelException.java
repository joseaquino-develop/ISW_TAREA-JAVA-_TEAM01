package vallegran.edu.pe.model;

/**
 * Excepción lanzada cuando no se puede establecer o cerrar el túnel SSH
 * (JSch) hacia la base de datos remota.
 */
public class SshTunnelException extends Exception {

    public SshTunnelException(String mensaje) {
        super(mensaje);
    }

    public SshTunnelException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
