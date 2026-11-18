package vallegran.edu.pe.model;

import java.util.Objects;

/** Maestro 1: tabla categorias (filtros de la carta en la web). */
public class Categoria {

    private int idCategoria;
    private String nombre;
    private String descripcion;
    private boolean estado = true;

    public Categoria() {
    }

    public Categoria(int idCategoria, String nombre, String descripcion, boolean estado) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        return idCategoria == ((Categoria) o).idCategoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }

    /** Lo que se ve en los ComboBox. */
    @Override
    public String toString() {
        return nombre;
    }
}
