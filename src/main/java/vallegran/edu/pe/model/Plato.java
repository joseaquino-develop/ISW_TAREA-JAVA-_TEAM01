package vallegran.edu.pe.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/** Maestro 2: tabla platos (tarjetas de la carta en la web). */
public class Plato {

    private int idPlato;
    private int idCategoria;
    private String categoriaNombre; // viene del JOIN, solo para mostrar
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private boolean disponible = true;
    private Timestamp fechaActualizacion;

    public Plato() {
    }

    public int getIdPlato() { return idPlato; }
    public void setIdPlato(int idPlato) { this.idPlato = idPlato; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Timestamp getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plato)) return false;
        return idPlato == ((Plato) o).idPlato;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlato);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
