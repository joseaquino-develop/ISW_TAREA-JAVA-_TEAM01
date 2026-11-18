package vallegran.edu.pe.view;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vallegran.edu.pe.controller.LoginController;
import vallegran.edu.pe.controller.MainController;
import vallegran.edu.pe.model.Usuario;

import java.net.URL;

/** Ventana principal: alterna entre el login y el panel de administración. */
public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("CHIFA FUSION · Administración");
        mostrarLogin();
        stage.show();
    }

    private void mostrarLogin() {
        LoginView vista = new LoginView();
        new LoginController(vista, this::mostrarDashboard);
        mostrarEscena(vista.getRoot(), 480, 560);
    }

    private void mostrarDashboard(Usuario usuario) {
        DashboardView vista = new DashboardView();
        new MainController(usuario, vista, getHostServices(), this::mostrarLogin);
        mostrarEscena(vista.getRoot(), 1200, 740);
    }

    private void mostrarEscena(Parent root, double ancho, double alto) {
        Scene escena = new Scene(root, ancho, alto);
        URL css = Main.class.getResource("/vallegran/edu/pe/styles.css");
        if (css != null) {
            escena.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(escena);
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
