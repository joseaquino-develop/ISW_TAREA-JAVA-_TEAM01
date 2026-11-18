package vallegran.edu.pe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioDAO {

    /** Devuelve el usuario si el usuario y la clave son correctos y está activo. */
    public Optional<Usuario> autenticar(String usuario, String clave) throws SQLException {
        String sql = "SELECT id_usuario, usuario, nombre_completo, rol "
                + "FROM usuarios WHERE usuario = ? AND clave_hash = ? AND estado = TRUE";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, GenerarHash.sha256(clave));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("usuario"),
                            rs.getString("nombre_completo"),
                            rs.getString("rol")));
                }
            }
        }
        return Optional.empty();
    }
}
