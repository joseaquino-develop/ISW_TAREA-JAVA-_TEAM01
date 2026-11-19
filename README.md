# ISW_TAREA_TEAM01_java · Panel de escritorio de CHIFA FUSION

App de escritorio (JavaFX 17, MVC en Java puro, sin FXML) que hace el **CRUD de 2 maestros**
de la web CHIFA FUSION (Flask + MySQL `chifa_fusion_db`):

| Maestro | Tabla | Qué cambia en la web |
|---|---|---|
| Categorías | `categorias` | Botones de filtro de `/carta` (solo las **activas**) |
| Platos | `platos` | Tarjetas de `/carta`: nombre, descripción, precio, imagen (solo los **disponibles**) |

Además trae **login** contra la tabla `usuarios` (clave guardada como hash SHA-256).

## 1. Base de datos (MySQL Workbench)
1. Abre `src/main/resources/sql/chifa_fusion_db.sql` en Workbench y ejecútalo completo (rayo).
   - Es el `schema.sql` del equipo **sin cambios** + la tabla `usuarios` con el usuario `admin`.
   - Se puede ejecutar varias veces sin duplicar datos.
2. Si ya tenías la BD creada con el `schema.sql` original, igual ejecútalo: solo agrega `usuarios`.

## 2. Configuración
1. Revisa `config.properties` (raíz del proyecto): host, puerto, usuario y contraseña de tu MySQL,
   más los parámetros SSH (`ssh.host`, `ssh.port`, `ssh.user`, `ssh.key` con la ruta a tu llave `.pem`).
   (`config.properties.example` es la plantilla; `config.properties` está en `.gitignore`.)
2. La web Flask debe apuntar a **la misma BD**: en su `.env` usa el mismo `DB_HOST`, `DB_PORT`, `DB_NAME`,
   `DB_USER` y `DB_PASSWORD`.

## Túnel SSH automático (JSch)
- La app usa **com.jcraft:jsch** (ver `pom.xml`). Antes de cada conexión JDBC, `Conexion.getConnection()`
  inicia una sesión SSH con la llave privada `.pem` y abre un port forwarding local:
  `localhost:3306 → database-1.cnqjpfxoqmvf.us-east-1.rds.amazonaws.com:3306` vía la EC2 bastión.
- El túnel se cierra solo al cerrar la ventana principal o al apagar la JVM (shutdown hook).
- Requisito: tener la llave `.pem` accesible en la ruta indicada en `ssh.key` y permisos de red
  hacia la instancia bastión (puerto 22).

## 3. Abrir en IntelliJ IDEA
1. `File > Open` y elige la carpeta `ISW_TAREA_TEAM01_java` (la que tiene el `pom.xml`).
2. Deja que Maven descargue las dependencias. Usa **JDK 17**.
3. Ejecuta `src/main/java/vallegran/edu/pe/view/Launcher.java` (botón ▶ junto al `main`).
   - También funciona: `mvn javafx:run`.
4. Ingresa con **admin / admin123**.

## 4. Probar la relación app ↔ MySQL ↔ web
1. Levanta la web: en la carpeta de la web, `python app.py` y abre http://localhost:5000/carta.
2. En la app, pestaña **Platos**: crea un plato, edita un precio o desmarca *Disponible*.
3. En Workbench ejecuta el bloque 2 o 3 de `mysql-workbench.sql`: el cambio ya está en la tabla.
4. En el navegador, **F5** en `/carta`: el cambio aparece (la web lee la BD en cada visita).
5. Prueba inversa: cambia algo con un `UPDATE` en Workbench y pulsa **Recargar desde BD** en la app.

## Notas
- Una categoría **inactiva** deja de verse como filtro en la web, pero sus platos disponibles siguen apareciendo en "Todos".
- No se puede eliminar una categoría que tiene platos (clave foránea): desmárcala como inactiva o mueve/elimina sus platos.
- Si la imagen de un plato queda vacía, la web usa una imagen por defecto.
- Para crear otro usuario: ejecuta `GenerarHash.main` con la clave y pega el hash en `usuarios.clave_hash`.

## Estructura
```
src/main/java/vallegran/edu/pe/
  controller/  LoginController, MainController
  model/       Conexion, Config, GenerarHash, Usuario(+DAO),
               Categoria(+DAO, Validator), Plato(+DAO, Validator)
  view/        Launcher, Main, LoginView, DashboardView
src/main/resources/
  sql/         chifa_fusion_db.sql, mysql-workbench.sql
  vallegran/edu/pe/styles.css
```
