package vallegran.edu.pe.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import vallegran.edu.pe.model.Categoria;
import vallegran.edu.pe.model.Plato;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Panel de administración con los 2 maestros:
 *  - Categorías (filtros de la carta)
 *  - Platos (tarjetas de la carta)
 * Solo construye la interfaz; la lógica está en MainController.
 */
public class DashboardView {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BorderPane root = new BorderPane();

    // ---- barra superior / estado ----
    public final Label lblUsuario = new Label();
    public final Button btnRecargar = new Button("Recargar desde BD");
    public final Button btnVerWeb = new Button("Ver carta en la web");
    public final Button btnSalir = new Button("Cerrar sesión");
    public final Label lblEstado = new Label("Listo.");

    // ---- maestro 1: categorías ----
    public final TableView<Categoria> tblCategorias = new TableView<>();
    public final Label lblCatModo = new Label("Nueva categoría");
    public final TextField txtCatNombre = new TextField();
    public final TextArea txtCatDescripcion = new TextArea();
    public final CheckBox chkCatActiva = new CheckBox("Activa (se muestra como filtro en la web)");
    public final Button btnCatGuardar = new Button("Guardar");
    public final Button btnCatNuevo = new Button("Nuevo");
    public final Button btnCatEliminar = new Button("Eliminar");
    public final Label lblCatMensaje = new Label();

    // ---- maestro 2: platos ----
    public final TextField txtBuscarPlato = new TextField();
    public final TableView<Plato> tblPlatos = new TableView<>();
    public final Label lblPlatoModo = new Label("Nuevo plato");
    public final ComboBox<Categoria> cmbCategoria = new ComboBox<>();
    public final TextField txtPlatoNombre = new TextField();
    public final TextArea txtPlatoDescripcion = new TextArea();
    public final TextField txtPlatoPrecio = new TextField();
    public final TextField txtPlatoImagen = new TextField();
    public final ImageView imgPreview = new ImageView();
    public final Label lblPreviewInfo = new Label("Sin imagen");
    public final CheckBox chkPlatoDisponible = new CheckBox("Disponible (se muestra en la web)");
    public final Button btnPlatoGuardar = new Button("Guardar");
    public final Button btnPlatoNuevo = new Button("Nuevo");
    public final Button btnPlatoEliminar = new Button("Eliminar");
    public final Label lblPlatoMensaje = new Label();

    public DashboardView() {
        TabPane tabs = new TabPane(crearTabCategorias(), crearTabPlatos());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        lblEstado.getStyleClass().add("status-text");
        HBox barraEstado = new HBox(lblEstado);
        barraEstado.setPadding(new Insets(8, 20, 8, 20));
        barraEstado.getStyleClass().add("status-bar");

        root.setTop(crearBarraSuperior());
        root.setCenter(tabs);
        root.setBottom(barraEstado);
        root.getStyleClass().add("dashboard-root");
    }

    public Parent getRoot() {
        return root;
    }

    public void setUsuario(String texto) {
        lblUsuario.setText(texto);
    }

    // =====================================================================
    // Barra superior
    // =====================================================================
    private Node crearBarraSuperior() {
        Label titulo = new Label("CHIFA FUSION");
        titulo.getStyleClass().add("app-title");
        Label subtitulo = new Label("Panel de administración de la carta · cambios en MySQL y en la web");
        subtitulo.getStyleClass().add("app-subtitle");
        VBox textos = new VBox(2, titulo, subtitulo);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        lblUsuario.getStyleClass().add("user-label");
        btnRecargar.getStyleClass().add("btn-light");
        btnVerWeb.getStyleClass().add("btn-gold");
        btnSalir.getStyleClass().add("btn-light");

        HBox barra = new HBox(10, textos, espacio, lblUsuario, btnRecargar, btnVerWeb, btnSalir);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(14, 20, 14, 20));
        barra.getStyleClass().add("top-bar");
        return barra;
    }

    // =====================================================================
    // Maestro 1: Categorías
    // =====================================================================
    private Tab crearTabCategorias() {
        TableColumn<Categoria, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdCategoria())));
        colId.setMaxWidth(70);
        colId.setMinWidth(50);

        TableColumn<Categoria, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));

        TableColumn<Categoria, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(d -> new SimpleStringProperty(nulo(d.getValue().getDescripcion())));

        TableColumn<Categoria, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isEstado() ? "Activa" : "Inactiva"));
        colEstado.setMaxWidth(100);
        colEstado.setMinWidth(80);

        tblCategorias.getColumns().add(colId);
        tblCategorias.getColumns().add(colNombre);
        tblCategorias.getColumns().add(colDescripcion);
        tblCategorias.getColumns().add(colEstado);
        tblCategorias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblCategorias.setPlaceholder(new Label("No hay categorías registradas."));
        tblCategorias.setRowFactory(tv -> new TableRow<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("row-inactiva");
                if (!empty && item != null && !item.isEstado()) {
                    getStyleClass().add("row-inactiva");
                }
            }
        });

        txtCatNombre.setPromptText("Ej. Arroces");
        txtCatDescripcion.setPromptText("Descripción breve (opcional)");
        txtCatDescripcion.setPrefRowCount(3);
        txtCatDescripcion.setWrapText(true);
        chkCatActiva.setSelected(true);

        btnCatGuardar.getStyleClass().add("btn-primary");
        btnCatNuevo.getStyleClass().add("btn-secondary");
        btnCatEliminar.getStyleClass().add("btn-danger");
        lblCatModo.getStyleClass().add("form-title");
        lblCatMensaje.setWrapText(true);

        HBox botones = new HBox(8, btnCatGuardar, btnCatNuevo, btnCatEliminar);

        VBox formulario = new VBox(12,
                lblCatModo,
                campo("Nombre *", txtCatNombre),
                campo("Descripción", txtCatDescripcion),
                chkCatActiva,
                botones,
                lblCatMensaje);
        formulario.getStyleClass().add("form-card");
        formulario.setPadding(new Insets(18));
        formulario.setPrefWidth(340);
        formulario.setMinWidth(320);
        formulario.setMaxWidth(360);

        HBox contenido = new HBox(16, tblCategorias, formulario);
        HBox.setHgrow(tblCategorias, Priority.ALWAYS);
        contenido.setPadding(new Insets(16));

        Tab tab = new Tab("Categorías", contenido);
        tab.setClosable(false);
        return tab;
    }

    // =====================================================================
    // Maestro 2: Platos
    // =====================================================================
    private Tab crearTabPlatos() {
        TableColumn<Plato, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdPlato())));
        colId.setMaxWidth(60);
        colId.setMinWidth(45);

        TableColumn<Plato, String> colNombre = new TableColumn<>("Plato");
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));

        TableColumn<Plato, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(nulo(d.getValue().getCategoriaNombre())));

        TableColumn<Plato, String> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPrecio() == null ? "" : String.format(Locale.US, "S/ %.2f", d.getValue().getPrecio())));
        colPrecio.setMaxWidth(100);
        colPrecio.setMinWidth(80);

        TableColumn<Plato, String> colDisponible = new TableColumn<>("Disponible");
        colDisponible.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isDisponible() ? "Sí" : "No"));
        colDisponible.setMaxWidth(90);
        colDisponible.setMinWidth(70);

        TableColumn<Plato, String> colActualizado = new TableColumn<>("Actualizado");
        colActualizado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaActualizacion() == null ? ""
                        : d.getValue().getFechaActualizacion().toLocalDateTime().format(FECHA)));
        colActualizado.setMinWidth(120);

        tblPlatos.getColumns().add(colId);
        tblPlatos.getColumns().add(colNombre);
        tblPlatos.getColumns().add(colCategoria);
        tblPlatos.getColumns().add(colPrecio);
        tblPlatos.getColumns().add(colDisponible);
        tblPlatos.getColumns().add(colActualizado);
        tblPlatos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblPlatos.setPlaceholder(new Label("No hay platos registrados."));
        tblPlatos.setRowFactory(tv -> new TableRow<Plato>() {
            @Override
            protected void updateItem(Plato item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("row-inactiva");
                if (!empty && item != null && !item.isDisponible()) {
                    getStyleClass().add("row-inactiva");
                }
            }
        });

        txtBuscarPlato.setPromptText("Buscar por nombre, descripción o categoría…");

        VBox izquierda = new VBox(10, txtBuscarPlato, tblPlatos);
        VBox.setVgrow(tblPlatos, Priority.ALWAYS);

        cmbCategoria.setPromptText("Selecciona una categoría");
        cmbCategoria.setMaxWidth(Double.MAX_VALUE);
        txtPlatoNombre.setPromptText("Ej. Arroz chaufa especial");
        txtPlatoDescripcion.setPromptText("Descripción que verá el cliente en la carta");
        txtPlatoDescripcion.setPrefRowCount(3);
        txtPlatoDescripcion.setWrapText(true);
        txtPlatoPrecio.setPromptText("Ej. 25.50");
        txtPlatoImagen.setPromptText("https://… (opcional)");
        chkPlatoDisponible.setSelected(true);

        imgPreview.setFitWidth(290);
        imgPreview.setFitHeight(130);
        imgPreview.setPreserveRatio(true);
        StackPane cajaPreview = new StackPane(imgPreview);
        cajaPreview.setMinHeight(130);
        cajaPreview.setPrefHeight(130);
        cajaPreview.getStyleClass().add("preview-box");
        lblPreviewInfo.getStyleClass().add("hint-text");

        btnPlatoGuardar.getStyleClass().add("btn-primary");
        btnPlatoNuevo.getStyleClass().add("btn-secondary");
        btnPlatoEliminar.getStyleClass().add("btn-danger");
        lblPlatoModo.getStyleClass().add("form-title");
        lblPlatoMensaje.setWrapText(true);

        HBox botones = new HBox(8, btnPlatoGuardar, btnPlatoNuevo, btnPlatoEliminar);

        VBox formulario = new VBox(10,
                lblPlatoModo,
                campo("Categoría *", cmbCategoria),
                campo("Nombre *", txtPlatoNombre),
                campo("Descripción", txtPlatoDescripcion),
                campo("Precio (S/) *", txtPlatoPrecio),
                campo("URL de la imagen", txtPlatoImagen),
                cajaPreview,
                lblPreviewInfo,
                chkPlatoDisponible,
                botones,
                lblPlatoMensaje);
        formulario.getStyleClass().add("form-card");
        formulario.setPadding(new Insets(18));

        ScrollPane scroll = new ScrollPane(formulario);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(360);
        scroll.setMinWidth(340);
        scroll.setMaxWidth(380);
        scroll.getStyleClass().add("form-scroll");

        HBox contenido = new HBox(16, izquierda, scroll);
        HBox.setHgrow(izquierda, Priority.ALWAYS);
        contenido.setPadding(new Insets(16));

        Tab tab = new Tab("Platos de la carta", contenido);
        tab.setClosable(false);
        return tab;
    }

    // =====================================================================
    // Utilidades
    // =====================================================================
    private VBox campo(String etiqueta, Node control) {
        Label lbl = new Label(etiqueta);
        lbl.getStyleClass().add("field-label");
        return new VBox(4, lbl, control);
    }

    private String nulo(String valor) {
        return valor == null ? "" : valor;
    }
}
