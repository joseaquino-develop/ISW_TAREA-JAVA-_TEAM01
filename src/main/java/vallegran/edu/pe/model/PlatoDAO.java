package vallegran.edu.pe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlatoDAO {

    private static final String BASE_SELECT =
            "SELECT p.id_plato, p.id_categoria, c.nombre AS categoria_nombre, p.nombre, p.descripcion, "
                    + "p.precio, p.imagen_url, p.disponible, p.fecha_actualizacion "
                    + "FROM platos p INNER JOIN categorias c ON c.id_categoria = p.id_categoria ";

    private Plato mapear(ResultSet rs) throws SQLException {
        Plato p = new Plato();
        p.setIdPlato(rs.getInt("id_plato"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setCategoriaNombre(rs.getString("categoria_nombre"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setImagenUrl(rs.getString("imagen_url"));
        p.setDisponible(rs.getBoolean("disponible"));
        p.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return p;
    }

    /** Lista todos los platos (disponibles o no). La web solo muestra los disponibles. */
    public List<Plato> listar() throws SQLException {
        return buscar("");
    }

    /** Busca por nombre, descripción o categoría. Texto vacío = todos. */
    public List<Plato> buscar(String texto) throws SQLException {
        String filtro = "%" + (texto == null ? "" : texto.trim()) + "%";
        String sql = BASE_SELECT
                + "WHERE p.nombre LIKE ? OR p.descripcion LIKE ? OR c.nombre LIKE ? "
                + "ORDER BY p.id_plato DESC";
        List<Plato> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public int insertar(Plato p) throws SQLException {
        String sql = "INSERT INTO platos (id_categoria, nombre, descripcion, precio, imagen_url, disponible) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean actualizar(Plato p) throws SQLException {
        String sql = "UPDATE platos SET id_categoria = ?, nombre = ?, descripcion = ?, precio = ?, "
                + "imagen_url = ?, disponible = ? WHERE id_plato = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, p);
            ps.setInt(7, p.getIdPlato());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idPlato) throws SQLException {
        String sql = "DELETE FROM platos WHERE id_plato = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPlato);
            return ps.executeUpdate() > 0;
        }
    }

    private void asignar(PreparedStatement ps, Plato p) throws SQLException {
        ps.setInt(1, p.getIdCategoria());
        ps.setString(2, p.getNombre().trim());
        ps.setString(3, vacioANull(p.getDescripcion()));
        ps.setBigDecimal(4, p.getPrecio());
        ps.setString(5, vacioANull(p.getImagenUrl()));
        ps.setBoolean(6, p.isDisponible());
    }

    private String vacioANull(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
