# Microservicio Usuarios

Servicio independiente para registrar propietarios, clientes y empleados, autenticar con JWT y asignar roles. Las contraseñas se guardan con BCrypt. El rol se determina en el servidor.

## Requisitos

- Java 21.
- MySQL 8.x con la base `plazoleta_usuarios` creada.
- Maven Wrapper incluido.

La aplicación no crea la base MySQL. Para pruebas se usa H2 en memoria.

## Configuración

Exporta las variables de `.env.example` al entorno antes de arrancar; Spring Boot no carga archivos `.env` automáticamente. `JWT_SECRET` debe ser aleatorio, tener al menos 32 bytes y coincidir con el de Plazoleta. `DB_PASSWORD` y `JWT_SECRET` no se guardan en el repositorio.

El administrador inicial se crea únicamente cuando se configuran las seis variables `ADMIN_*`. Si el correo ya existe, el proceso no lo vuelve a crear. No hay una ruta pública para crear administradores.

## Compilar, probar y ejecutar

Desde PowerShell en la carpeta `usuarios`:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/plazoleta_usuarios'
$env:DB_USERNAME = 'plazoleta_app'
$env:DB_PASSWORD = '<contraseña local>'
$env:JWT_SECRET = '<secreto aleatorio compartido de 32 bytes o más>'
$env:ADMIN_EMAIL = '<correo del administrador>'
$env:ADMIN_PASSWORD = '<contraseña inicial>'
$env:ADMIN_NAME = '<nombre>'
$env:ADMIN_LAST_NAME = '<apellido>'
$env:ADMIN_DOCUMENT = '<documento numérico>'
$env:ADMIN_PHONE = '<celular>'
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

El puerto predeterminado es `8081`. Hibernate actualiza las tablas existentes (`ddl-auto=update`).

## Endpoints

- `POST /api/v1/auth/login` — correo y clave; devuelve JWT.
- `POST /api/v1/usuarios/propietarios` — requiere rol `ADMIN`.
- `POST /api/v1/usuarios/clientes` — registro público; siempre asigna `CLIENTE`. Acepta `idRol` por compatibilidad con el contrato del reto, pero no lo usa para determinar el rol.
- `POST /api/v1/usuarios/empleados` — requiere `PROPIETARIO` y un `restauranteId`; verifica propiedad contra Plazoleta usando el JWT autenticado. `idRol`, si se envía, no cambia el rol final `EMPLEADO`.
- `GET /api/v1/usuarios/{id}/rol` — requiere `ADMIN`; devuelve solo id y rol para la validación entre servicios.

Swagger UI: `http://localhost:8081/swagger-ui.html`. OpenAPI: `http://localhost:8081/v3/api-docs`.

Las pruebas usan H2 y no llaman por HTTP a Plazoleta.
