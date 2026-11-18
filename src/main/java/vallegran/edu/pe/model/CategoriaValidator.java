package vallegran.edu.pe.model;

import java.util.ArrayList;
import java.util.List;

public final class CategoriaValidator {

    private CategoriaValidator() {
    }

    /** Devuelve la lista de errores (vacía si todo está bien). */
    public static List<String> validar(Categoria c) {
        List<String> errores = new ArrayList<>();
        String nombre = c.getNombre() == null ? "" : c.getNombre().trim();
        if (nombre.isEmpty()) {
            errores.add("El nombre de la categoría es obligatorio.");
        } else if (nombre.length() > 100) {
            errores.add("El nombre no puede superar 100 caracteres.");
        }
        if (c.getDescripcion() != null && c.getDescripcion().length() > 255) {
            errores.add("La descripción no puede superar 255 caracteres.");
        }
        return errores;
    }
}
