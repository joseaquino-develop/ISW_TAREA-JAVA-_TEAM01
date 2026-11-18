package vallegran.edu.pe.controller;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import vallegran.edu.pe.model.Categoria;
import vallegran.edu.pe.model.CategoriaDAO;
import vallegran.edu.pe.model.CategoriaValidator;
import vallegran.edu.pe.model.Config;
import vallegran.edu.pe.model.Plato;
import vallegran.edu.pe.model.PlatoDAO;
import vallegran.edu.pe.model.PlatoValidator;
import vallegran.edu.pe.model.Usuario;
import vallegran.edu.pe.view.DashboardView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador del panel: CRUD de Categorías y Platos contra MySQL.
 * La web Flask lee las mismas tablas en cada petición, así que cualquier cambio
 * guardado aquí se ve en la web al recargar la página (F5).
 */
public class MainController {

    private final DashboardView vista;
    private final HostServices hostServices;
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final PlatoDAO platoDAO = new PlatoDAO();

    private Categoria categoriaActual; // null = se está creando una nueva
    private Plato platoActual;         // null = se está creando uno nuevo

    public MainController(Usuario usuario, DashboardView vista, HostServices hostServices,
                          Runnable alCerrarSesion) {
        this.vista = vista;
        this.hostServices = hostServices;

        vista.setUsuario("👤 " + usuario.getNombreCompleto());
        vista.btnSalir.setOnAction(e -> alCerrarSesion.run());
        vista.btnRecargar.setOnAction(e -> {
            recargarTodo();
            estado("Datos recargados desde MySQL.");
        });
        vista.btnVerWeb.setOnAction(e -> abrirCartaWeb());

        configurarCategorias();
        configurarPlatos();

        recargarTodo();
        nuevaCategoria();
        nuevoPlato();
    }

    // =====================================================================
    // Carga de datos
    // =====================================================================
    private void recargarTodo() {
        try {
            vista.tblCategorias.setItems(FXCollections.observableArrayList(categoriaDAO.listar()));
            vista.cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.listarActivas()));
            vista.tblPlatos.setItems(FXCollections.observableArrayList(
                    platoDAO.buscar(vista.txtBuscarPlato.getText())));
        } catch (SQLException ex) {
            error("No se pudo leer la base de datos: " + ex.getMessage());
        }
    }

    // =====================================================================
    // Maestro 1: Categorías
    // =====================================================================
    private void configurarCategorias() {
        vista.tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, sel) -> {
            if (sel != null) {
                cargarCategoriaEnFormulario(sel);
            }
        });
        vista.btnCatNuevo.setOnAction(e -> {
            vista.tblCategorias.getSelectionModel().clearSelection();
            nuevaCategoria();
        });
        vista.btnCatGuardar.setOnAction(e -> guardarCategoria());
        vista.btnCatEliminar.setOnAction(e -> eliminarCategoria());
    }

    private void nuevaCategoria() {
        categoriaActual = null;
        vista.lblCatModo.setText("Nueva categoría");
        vista.txtCatNombre.clear();
        vista.txtCatDescripcion.clear();
        vista.chkCatActiva.setSelected(true);
        mensaje(vista.lblCatMensaje, "", false);
    }

    private void cargarCategoriaEnFormulario(Categoria c) {
        categoriaActual = c;
        vista.lblCatModo.setText("Editando categoría #" + c.getIdCategoria());
        vista.txtCatNombre.setText(c.getNombre());
        vista.txtCatDescripcion.setText(c.getDescripcion() == null ? "" : c.getDescripcion());
        vista.chkCatActiva.setSelected(c.isEstado());
        mensaje(vista.lblCatMensaje, "", false);
    }

    private void guardarCategoria() {
        Categoria c = new Categoria();
        c.setIdCategoria(categoriaActual == null ? 0 : categoriaActual.getIdCategoria());
        c.setNombre(vista.txtCatNombre.getText().trim());
        c.setDescripcion(vista.txtCatDescripcion.getText().trim());
        c.setEstado(vista.chkCatActiva.isSelected());

        List<String> errores = CategoriaValidator.validar(c);
        if (!errores.isEmpty()) {
            mensaje(vista.lblCatMensaje, String.join("\n", errores), true);
            return;
        }

        try {
            if (categoriaDAO.existeNombre(c.getNombre(), c.getIdCategoria())) {
                mensaje(vista.lblCatMensaje, "Ya existe una categoría con ese nombre.", true);
                return;
            }

            int id;
            String texto;
            if (c.getIdCategoria() == 0) {
                id = categoriaDAO.insertar(c);
                texto = "Categoría «" + c.getNombre() + "» creada.";
            } else {
                categoriaDAO.actualizar(c);
                id = c.getIdCategoria();
                texto = "Categoría «" + c.getNombre() + "» actualizada.";
            }

            recargarTodo();
            seleccionarCategoria(id);
            mensaje(vista.lblCatMensaje, texto, false);
            estadoConWeb(texto);
        } catch (SQLException ex) {
            mensaje(vista.lblCatMensaje, "No se pudo guardar: " + ex.getMessage(), true);
        }
    }

    private void eliminarCategoria() {
        if (categoriaActual == null) {
            mensaje(vista.lblCatMensaje, "Selecciona una categoría de la tabla para eliminarla.", true);
            return;
        }
        if (!confirmar("Eliminar categoría",
                "¿Eliminar la categoría «" + categoriaActual.getNombre() + "»?\nEsta acción no se puede deshacer.")) {
            return;
        }

        try {
            categoriaDAO.eliminar(categoriaActual.getIdCategoria());
            String texto = "Categoría «" + categoriaActual.getNombre() + "» eliminada.";
            recargarTodo();
            nuevaCategoria();
            mensaje(vista.lblCatMensaje, texto, false);
            estadoConWeb(texto);
        } catch (SQLIntegrityConstraintViolationException ex) {
            mensaje(vista.lblCatMensaje, "No se puede eliminar: la categoría tiene platos asociados. "
                    + "Desmarca «Activa» para ocultarla de la web, o elimina/mueve sus platos primero.", true);
        } catch (SQLException ex) {
            mensaje(vista.lblCatMensaje, "No se pudo eliminar: " + ex.getMessage(), true);
        }
    }

    private void seleccionarCategoria(int id) {
        for (Categoria c : vista.tblCategorias.getItems()) {
            if (c.getIdCategoria() == id) {
                vista.tblCategorias.getSelectionModel().select(c);
                vista.tblCategorias.scrollTo(c);
                return;
            }
        }
    }

    // =====================================================================
    // Maestro 2: Platos
    // =====================================================================
    private void configurarPlatos() {
        vista.tblPlatos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, sel) -> {
            if (sel != null) {
                cargarPlatoEnFormulario(sel);
            }
        });
        vista.txtBuscarPlato.textProperty().addListener((obs, anterior, nuevo) -> {
            try {
                vista.tblPlatos.setItems(FXCollections.observableArrayList(platoDAO.buscar(nuevo)));
            } catch (SQLException ex) {
                error("No se pudo buscar: " + ex.getMessage());
            }
        });
        // La vista previa se actualiza al salir del campo de imagen o al pulsar Enter.
        vista.txtPlatoImagen.setOnAction(e -> mostrarVistaPrevia(vista.txtPlatoImagen.getText()));
        vista.txtPlatoImagen.focusedProperty().addListener((obs, antes, ahora) -> {
            if (!ahora) {
                mostrarVistaPrevia(vista.txtPlatoImagen.getText());
            }
        });

        vista.btnPlatoNuevo.setOnAction(e -> {
            vista.tblPlatos.getSelectionModel().clearSelection();
            nuevoPlato();
        });
        vista.btnPlatoGuardar.setOnAction(e -> guardarPlato());
        vista.btnPlatoEliminar.setOnAction(e -> eliminarPlato());
    }

    private void nuevoPlato() {
        platoActual = null;
        vista.lblPlatoModo.setText("Nuevo plato");
        vista.cmbCategoria.setValue(null);
        vista.txtPlatoNombre.clear();
        vista.txtPlatoDescripcion.clear();
        vista.txtPlatoPrecio.clear();
        vista.txtPlatoImagen.clear();
        vista.chkPlatoDisponible.setSelected(true);
        mostrarVistaPrevia("");
        mensaje(vista.lblPlatoMensaje, "", false);
    }

    private void cargarPlatoEnFormulario(Plato p) {
        platoActual = p;
        vista.lblPlatoModo.setText("Editando plato #" + p.getIdPlato());

        // Si la categoría del plato está inactiva no aparece en el combo: se agrega para poder editarlo.
        Categoria categoria = new Categoria(p.getIdCategoria(), p.getCategoriaNombre(), null, false);
        if (!vista.cmbCategoria.getItems().contains(categoria)) {
            vista.cmbCategoria.getItems().add(categoria);
        }
        vista.cmbCategoria.setValue(categoria);

        vista.txtPlatoNombre.setText(p.getNombre());
        vista.txtPlatoDescripcion.setText(p.getDescripcion() == null ? "" : p.getDescripcion());
        vista.txtPlatoPrecio.setText(p.getPrecio() == null ? "" : p.getPrecio().toPlainString());
        vista.txtPlatoImagen.setText(p.getImagenUrl() == null ? "" : p.getImagenUrl());
        vista.chkPlatoDisponible.setSelected(p.isDisponible());
        mostrarVistaPrevia(p.getImagenUrl());
        mensaje(vista.lblPlatoMensaje, "", false);
    }

    private void guardarPlato() {
        Plato p = new Plato();
        p.setIdPlato(platoActual == null ? 0 : platoActual.getIdPlato());
        Categoria categoria = vista.cmbCategoria.getValue();
        p.setIdCategoria(categoria == null ? 0 : categoria.getIdCategoria());
        p.setNombre(vista.txtPlatoNombre.getText().trim());
        p.setDescripcion(vista.txtPlatoDescripcion.getText().trim());
        p.setPrecio(leerPrecio(vista.txtPlatoPrecio.getText()));
        p.setImagenUrl(vista.txtPlatoImagen.getText().trim());
        p.setDisponible(vista.chkPlatoDisponible.isSelected());

        List<String> errores = PlatoValidator.validar(p);
        if (!errores.isEmpty()) {
            mensaje(vista.lblPlatoMensaje, String.join("\n", errores), true);
            return;
        }

        try {
            int id;
            String texto;
            if (p.getIdPlato() == 0) {
                id = platoDAO.insertar(p);
                texto = "Plato «" + p.getNombre() + "» creado.";
            } else {
                platoDAO.actualizar(p);
                id = p.getIdPlato();
                texto = "Plato «" + p.getNombre() + "» actualizado.";
            }

            recargarTodo();
            seleccionarPlato(id);
            mensaje(vista.lblPlatoMensaje, texto, false);
            estadoConWeb(texto);
        } catch (SQLException ex) {
            mensaje(vista.lblPlatoMensaje, "No se pudo guardar: " + ex.getMessage(), true);
        }
    }

    private void eliminarPlato() {
        if (platoActual == null) {
            mensaje(vista.lblPlatoMensaje, "Selecciona un plato de la tabla para eliminarlo.", true);
            return;
        }
        if (!confirmar("Eliminar plato",
                "¿Eliminar el plato «" + platoActual.getNombre() + "»?\nEsta acción no se puede deshacer.")) {
            return;
        }

        try {
            platoDAO.eliminar(platoActual.getIdPlato());
            String texto = "Plato «" + platoActual.getNombre() + "» eliminado.";
            recargarTodo();
            nuevoPlato();
            mensaje(vista.lblPlatoMensaje, texto, false);
            estadoConWeb(texto);
        } catch (SQLException ex) {
            mensaje(vista.lblPlatoMensaje, "No se pudo eliminar: " + ex.getMessage(), true);
        }
    }

    private void seleccionarPlato(int id) {
        for (Plato p : vista.tblPlatos.getItems()) {
            if (p.getIdPlato() == id) {
                vista.tblPlatos.getSelectionModel().select(p);
                vista.tblPlatos.scrollTo(p);
                return;
            }
        }
    }

    /** Convierte el texto a BigDecimal con 2 decimales (acepta coma o punto). Null si no es válido. */
    private BigDecimal leerPrecio(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void mostrarVistaPrevia(String url) {
        if (url == null || url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
            vista.imgPreview.setImage(null);
            vista.lblPreviewInfo.setText("Sin imagen (la web usará una imagen por defecto)");
            return;
        }
        Image imagen = new Image(url.trim(), 580, 260, true, true, true); // carga en segundo plano
        imagen.errorProperty().addListener((obs, antes, hayError) -> {
            if (hayError) {
                vista.imgPreview.setImage(null);
                vista.lblPreviewInfo.setText("No se pudo cargar la vista previa de esa URL.");
            }
        });
        vista.imgPreview.setImage(imagen);
        vista.lblPreviewInfo.setText("Vista previa de la imagen");
    }

    // =====================================================================
    // Utilidades de interfaz
    // =====================================================================
    private void abrirCartaWeb() {
        String url = Config.webUrl() + "/carta";
        hostServices.showDocument(url);
        estado("Abriendo " + url + " (la web debe estar corriendo con python app.py).");
    }

    private boolean confirmar(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private void mensaje(Label etiqueta, String texto, boolean esError) {
        etiqueta.getStyleClass().removeAll("msg-error", "msg-ok");
        etiqueta.getStyleClass().add(esError ? "msg-error" : "msg-ok");
        etiqueta.setText(texto);
    }

    private void estado(String texto) {
        vista.lblEstado.setText(texto);
    }

    private void estadoConWeb(String texto) {
        estado("✔ " + texto + "  Recarga " + Config.webUrl() + "/carta (F5) para verlo en la web.");
    }

    private void error(String texto) {
        vista.lblEstado.setText("✖ " + texto);
        Alert alerta = new Alert(Alert.AlertType.ERROR, texto, ButtonType.OK);
        alerta.setTitle("Error de base de datos");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
