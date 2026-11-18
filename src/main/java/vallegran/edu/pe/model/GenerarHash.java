package vallegran.edu.pe.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Genera el hash SHA-256 (hexadecimal) de una clave.
 * Ejecuta main() para obtener el hash de una nueva contraseña y pegarlo en la tabla usuarios.
 */
public final class GenerarHash {

    private GenerarHash() {
    }

    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static void main(String[] args) {
        String clave = args.length > 0 ? args[0] : "admin123";
        System.out.println("Clave  : " + clave);
        System.out.println("SHA-256: " + sha256(clave));
    }
}
