# Microservicio Plazoleta

Servicio independiente para administrar restaurantes y platos, y listar restaurantes para clientes. La identidad y los roles se validan mediante JWT emitidos por Usuarios; los restaurantes y platos tienen su propia base de datos.

## Requisitos

- Java 21.
- MySQL 8.x con la base `plazoleta_restaurantes` creada.
- Microservicio Usuarios disponible para validar el rol de un propietario al crear un restaurante.
- Maven Wrapper incluido.

## Configuración

Exporta las variables de `.env.example` al entorno antes de arrancar; Spring Boot no carga archivos `.env` automáticamente. `JWT_SECRET` debe coincidir con Usuarios y tener al menos 32 bytes. No guardes el secreto ni la clave MySQL en Git.

## Compilar, probar y ejecutar

Desde PowerShell en la carpeta `plazoleta`:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/plazoleta_restaurantes'
$env:DB_USERNAME = 'plazoleta_app'
$env:DB_PASSWORD = '<contraseña local>'
$env:JWT_SECRET = '<el mismo secreto aleatorio que usa Usuarios>'
$env:USUARIOS_URL = 'http://localhost:8081'
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

El puerto predeterminado es `8082`. Hibernate actualiza las tablas existentes (`ddl-auto=update`). Las pruebas usan H2 y no llaman por HTTP a Usuarios.

## Endpoints

- `POST /api/v1/restaurantes` — requiere `ADMIN`; verifica por HTTP que el propietario exista y tenga rol `PROPIETARIO`.
- `GET /api/v1/restaurantes?page=0&size=10` — requiere `CLIENTE`; orden ascendente, tamaño entre 1 y 100 y solo `nombre` y `urlLogo` por restaurante.
- `POST /api/v1/restaurantes/{restauranteId}/platos` — requiere el propietario del restaurante.
- `PATCH /api/v1/platos/{platoId}` — permite actualizar solo `precio` y `descripcion`.
- `PUT /api/v1/platos/{platoId}/estado` — requiere el propietario del restaurante.
- `GET /api/v1/internal/restaurantes/{restauranteId}/propietarios/{propietarioId}` — contrato de verificación usado por Usuarios; requiere JWT `PROPIETARIO` con identidad coincidente.

Swagger UI: `http://localhost:8082/swagger-ui.html`. OpenAPI: `http://localhost:8082/v3/api-docs`.
