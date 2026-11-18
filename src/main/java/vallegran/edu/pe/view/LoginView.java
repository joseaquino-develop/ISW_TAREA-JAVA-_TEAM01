package vallegran.edu.pe.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Vista del login (solo construye la interfaz; la lógica está en LoginController). */
public class LoginView {

    private final StackPane root = new StackPane();

    public final TextField txtUsuario = new TextField();
    public final PasswordField txtClave = new PasswordField();
    public final Button btnIngresar = new Button("Ingresar");
    public final Label lblError = new Label();

    public LoginView() {
        Label logo = new Label("中");
        logo.getStyleClass().add("login-logo");

        Label titulo = new Label("CHIFA FUSION");
        titulo.getStyleClass().add("login-title");

        Label subtitulo = new Label("Administración de la carta");
        subtitulo.getStyleClass().add("login-subtitle");

        txtUsuario.setPromptText("Usuario");
        txtClave.setPromptText("Contraseña");

        btnIngresar.getStyleClass().add("btn-primary");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setDefaultButton(true);

        lblError.getStyleClass().add("msg-error");
        lblError.setWrapText(true);

        VBox tarjeta = new VBox(12, logo, titulo, subtitulo, txtUsuario, txtClave, btnIngresar, lblError);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(32));
        tarjeta.setMaxWidth(360);
        tarjeta.setMaxHeight(Region.USE_PREF_SIZE);
        tarjeta.getStyleClass().add("login-card");

        root.getChildren().add(tarjeta);
        root.getStyleClass().add("login-root");
    }

    public Parent getRoot() {
        return root;
    }
}
