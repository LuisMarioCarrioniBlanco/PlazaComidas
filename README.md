# PlazaComidas

Proyecto compuesto por dos microservicios Spring Boot:

- `usuarios`: registro, autenticación y gestión de usuarios.
- `plazoleta`: restaurantes y platos.

Cada servicio incluye su propio Maven Wrapper y README con requisitos, configuración y endpoints.

## Requisitos

- Java 21
- MySQL 8.x

## Ejecutar

Configura las variables descritas en el `.env.example` de cada servicio y ejecuta desde su carpeta:

```powershell
.\mvnw.cmd spring-boot:run
```

`usuarios` usa el puerto 8081 y `plazoleta` el 8082. Revisa los README de cada servicio para detalles de bases de datos y configuración.
