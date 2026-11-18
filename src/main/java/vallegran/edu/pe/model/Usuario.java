package vallegran.edu.pe.model;

public class Usuario {

    private int idUsuario;
    private String usuario;
    private String nombreCompleto;
    private String rol;

    public Usuario() {
    }

    public Usuario(int idUsuario, String usuario, String nombreCompleto, String rol) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return nombreCompleto + " (" + usuario + ")";
    }
}
