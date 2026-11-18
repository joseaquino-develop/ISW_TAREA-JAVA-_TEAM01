-- =========================================================================
-- CHIFA FUSION · consultas para MySQL Workbench
-- Úsalas para comprobar que lo que haces en la app de escritorio llega a la BD
-- (y de ahí a la web). Ejecuta cada bloque con el rayo (Ctrl+Enter).
-- =========================================================================

USE chifa_fusion_db;

-- 1) Maestro 1: categorías (la web las muestra como botones de filtro si estado = 1)
SELECT id_categoria, nombre, descripcion, estado
FROM categorias
ORDER BY id_categoria;

-- 2) Maestro 2: platos con su categoría, del más nuevo al más antiguo
SELECT p.id_plato, c.nombre AS categoria, p.nombre, p.precio,
       p.disponible, p.imagen_url, p.fecha_actualizacion
FROM platos p
INNER JOIN categorias c ON c.id_categoria = p.id_categoria
ORDER BY p.id_plato DESC;

-- 3) Lo que realmente muestra la web en /carta (solo platos disponibles)
SELECT p.id_plato, c.nombre AS categoria, p.nombre, p.precio
FROM platos p
INNER JOIN categorias c ON c.id_categoria = p.id_categoria
WHERE p.disponible = TRUE
ORDER BY p.id_plato DESC;

-- 4) Últimos platos modificados (fecha_actualizacion cambia sola con cada UPDATE)
SELECT id_plato, nombre, precio, disponible, fecha_actualizacion
FROM platos
ORDER BY fecha_actualizacion DESC
LIMIT 5;

-- 5) Cantidad de platos por categoría
SELECT c.nombre AS categoria, COUNT(p.id_plato) AS total_platos
FROM categorias c
LEFT JOIN platos p ON p.id_categoria = c.id_categoria
GROUP BY c.id_categoria, c.nombre
ORDER BY c.nombre;

-- 6) Usuarios que pueden entrar a la app de escritorio
SELECT id_usuario, usuario, nombre_completo, rol, estado FROM usuarios;

-- =========================================================================
-- PRUEBA INVERSA (opcional): cambiar desde Workbench y ver el cambio en la app
-- (botón "Recargar desde BD") y en la web (F5 en /carta).
-- Se usa la clave primaria en el WHERE por el "Safe Updates" de Workbench.
-- =========================================================================
-- UPDATE platos SET precio = 27.00 WHERE id_plato = 1;
-- UPDATE platos SET disponible = FALSE WHERE id_plato = 2;   -- desaparece de la web
-- UPDATE platos SET disponible = TRUE  WHERE id_plato = 2;   -- vuelve a aparecer
