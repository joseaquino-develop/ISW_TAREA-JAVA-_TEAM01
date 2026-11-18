package vallegran.edu.pe.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PlatoValidator {

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999.99");

    private PlatoValidator() {
    }

    /** Devuelve la lista de errores (vacía si todo está bien). */
    public static List<String> validar(Plato p) {
        List<String> errores = new ArrayList<>();

        if (p.getIdCategoria() <= 0) {
            errores.add("Selecciona una categoría.");
        }

        String nombre = p.getNombre() == null ? "" : p.getNombre().trim();
        if (nombre.isEmpty()) {
            errores.add("El nombre del plato es obligatorio.");
        } else if (nombre.length() > 150) {
            errores.add("El nombre no puede superar 150 caracteres.");
        }

        if (p.getPrecio() == null) {
            errores.add("El precio es obligatorio y debe ser un número (ej. 25.50).");
        } else if (p.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            errores.add("El precio debe ser mayor que 0.");
        } else if (p.getPrecio().compareTo(PRECIO_MAXIMO) > 0) {
            errores.add("El precio no puede superar S/ 9999.99.");
        }

        if (p.getDescripcion() != null && p.getDescripcion().length() > 1000) {
            errores.add("La descripción no puede superar 1000 caracteres.");
        }

        String img = p.getImagenUrl() == null ? "" : p.getImagenUrl().trim();
        if (!img.isEmpty()) {
            if (img.length() > 500) {
                errores.add("La URL de la imagen no puede superar 500 caracteres.");
            } else if (!(img.startsWith("http://") || img.startsWith("https://") || img.startsWith("/"))) {
                errores.add("La imagen debe ser una URL que empiece con http:// o https://");
            }
        }
        return errores;
    }
}
