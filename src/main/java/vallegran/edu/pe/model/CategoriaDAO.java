package vallegran.edu.pe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("estado"));
    }

    public List<Categoria> listar() throws SQLException {
        return consultar("SELECT id_categoria, nombre, descripcion, estado FROM categorias ORDER BY nombre");
    }

    /** Solo las activas: son las que la web muestra como filtro. */
    public List<Categoria> listarActivas() throws SQLException {
        return consultar("SELECT id_categoria, nombre, descripcion, estado FROM categorias "
                + "WHERE estado = TRUE ORDER BY nombre");
    }

    private List<Categoria> consultar(String sql) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public int insertar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categorias (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBoolean(3, c.isEstado());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, estado = ? WHERE id_categoria = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBoolean(3, c.isEstado());
            ps.setInt(4, c.getIdCategoria());
            return ps.executeUpdate() > 0;
        }
    }

    /** Falla con SQLIntegrityConstraintViolationException si la categoría tiene platos. */
    public boolean eliminar(int idCategoria) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeNombre(String nombre, int idExcluido) throws SQLException {
        String sql = "SELECT 1 FROM categorias WHERE LOWER(nombre) = LOWER(?) AND id_categoria <> ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
