package vallegran.edu.pe.controller;

import vallegran.edu.pe.model.Usuario;
import vallegran.edu.pe.model.UsuarioDAO;
import vallegran.edu.pe.view.LoginView;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

/** Valida el login contra la tabla usuarios de MySQL. */
public class LoginController {

    private final LoginView vista;
    private final Consumer<Usuario> alIngresar;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginController(LoginView vista, Consumer<Usuario> alIngresar) {
        this.vista = vista;
        this.alIngresar = alIngresar;

        vista.btnIngresar.setOnAction(e -> ingresar());
        vista.txtClave.setOnAction(e -> ingresar());
        vista.txtUsuario.setOnAction(e -> vista.txtClave.requestFocus());
    }

    private void ingresar() {
        String usuario = vista.txtUsuario.getText().trim();
        String clave = vista.txtClave.getText();

        if (usuario.isEmpty() || clave.isEmpty()) {
            vista.lblError.setText("Ingresa tu usuario y contraseña.");
            return;
        }

        try {
            Optional<Usuario> encontrado = usuarioDAO.autenticar(usuario, clave);
            if (encontrado.isPresent()) {
                vista.lblError.setText("");
                alIngresar.accept(encontrado.get());
            } else {
                vista.lblError.setText("Usuario o contraseña incorrectos.");
                vista.txtClave.clear();
                vista.txtClave.requestFocus();
            }
        } catch (SQLException ex) {
            vista.lblError.setText("No se pudo conectar a MySQL. Revisa config.properties y que la BD "
                    + "chifa_fusion_db exista.\nDetalle: " + ex.getMessage());
        }
    }
}
