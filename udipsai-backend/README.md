# UDIPSAI Backend Unificado

## Descripción
Backend unificado del Sistema de Turnos UDIPSAI que integra los tres microservicios originales en una sola aplicación:
- **Autenticación**: Gestión de login, JWT y seguridad
- **Usuarios**: Gestión de usuarios, roles, profesionales, pacientes, etc.
- **Citas**: Gestión de agendamiento y citas

## Estructura del Proyecto

```
udipsai-backend/
├── src/
│   ├── main/
│   │   ├── java/com/udipsai/backend/
│   │   │   ├── UdipsaiBackendApplication.java   # Clase principal
│   │   │   ├── auth/                             # Módulo de Autenticación
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── repository/
│   │   │   │   ├── service/
│   │   │   │   └── web/
│   │   │   │       ├── controller/
│   │   │   │       └── config/
│   │   │   ├── usuarios/                         # Módulo de Usuarios
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── view/
│   │   │   │   ├── service/
│   │   │   │   │   └── dto/
│   │   │   │   └── web/
│   │   │   │       ├── controller/
│   │   │   │       └── config/
│   │   │   ├── citas/                            # Módulo de Citas
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── view/
│   │   │   │   ├── service/
│   │   │   │   │   └── dto/
│   │   │   │   └── web/
│   │   │   │       ├── controller/
│   │   │   │       └── config/
│   │   │   └── common/                           # Componentes Comunes
│   │   │       ├── exception/                    # Manejo de excepciones
│   │   │       ├── config/                       # Configuraciones compartidas
│   │   │       └── util/                         # Utilidades
│   │   └── resources/
│   │       ├── application.properties            # Configuración unificada
│   │       ├── logback-spring.xml               # Configuración de logs
│   │       └── data.sql                         # Datos iniciales
│   └── test/
├── logs/                                         # Directorio de logs
├── pom.xml                                       # Dependencias Maven unificadas
├── Dockerfile                                    # Imagen Docker
└── README.md

```

## Consolidación Realizada

### 1. Excepciones Unificadas (`common.exception`)
Todas las excepciones personalizadas están ahora en el paquete común:
- `ResourceNotFoundException`: Recurso no encontrado (404)
- `DataConflictException`: Conflicto de datos (409)
- `InvalidRequestBodyException`: Petición inválida (400)
- `UnauthorizedAccessException`: Sin autorización (401)
- `GlobalExceptionHandler`: Manejador global de excepciones
- `ExceptionResponse`: Estructura de respuesta de errores

**Beneficios:**
- ✅ Manejo consistente de errores en todos los módulos
- ✅ Código DRY (Don't Repeat Yourself)
- ✅ Fácil mantenimiento y extensión

### 2. Persistencia Modular
Cada módulo mantiene sus entidades y repositorios organizados:
- **Auth**: Entidades de autenticación y usuarios base
- **Usuarios**: Perfiles completos (Admin, Profesional, Secretaria, etc.)
- **Citas**: Citas, pacientes y relaciones

**Ventajas:**
- ✅ Separación de responsabilidades
- ✅ Código organizado por dominio
- ✅ Fácil de navegar y mantener

### 3. Servicios de Negocio
Los servicios están agrupados por módulo funcional manteniendo la lógica de negocio separada.

### 4. Configuración Unificada
Un solo archivo `application.properties` con:
- Configuración de PostgreSQL (base de datos principal)
- Configuración de MySQL (para módulo de citas)
- Configuración de seguridad y JWT
- Actuator y métricas
- CORS y manejo de errores

## Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- PostgreSQL 12+

## Variables de Entorno

```bash
# PostgreSQL (única base de datos)
POSTGRESQL_URL=jdbc:postgresql://localhost:5432/udipsai
POSTGRESQL_USER=postgres
POSTGRESQL_PASSWORD=tu_password

# Frontend URL para CORS
FE_URL=http://localhost:4200

# JWT (opcional, tiene valores por defecto)
JWT_SECRET=Jeremy_25312
JWT_EXPIRATION_DAYS=15
JWT_ISSUER=UCACUE_UDIPSAI
```

## Compilación y Ejecución

### Desarrollo Local

```bash
# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run
```

### Con Docker

```bash
# Construir la imagen
docker build -t udipsai-backend .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e POSTGRESQL_URL=jdbc:postgresql://host.docker.internal:5432/udipsai \
  -e POSTGRESQL_USER=postgres \
  -e POSTGRESQL_PASSWORD=password \
  -e FE_URL=http://localhost:4200 \
  udipsai-backend
```

## Endpoints Principales

### Autenticación (`/auth`)
- `POST /auth/login` - Iniciar sesión
- `POST /auth/register` - Registrar usuario
- `POST /auth/refresh` - Renovar token

### Usuarios (`/usuarios`)
- `GET /usuarios` - Listar usuarios
- `GET /usuarios/{id}` - Obtener usuario
- `POST /usuarios` - Crear usuario
- `PUT /usuarios/{id}` - Actualizar usuario
- `DELETE /usuarios/{id}` - Eliminar usuario
- Endpoints específicos para Admin, Profesional, Secretaria, Coordinador, Pasante

### Citas (`/citas`)
- `GET /citas` - Listar citas
- `POST /citas` - Agendar cita
- `PUT /citas/{id}` - Actualizar cita
- `DELETE /citas/{id}` - Cancelar cita
- Endpoints para gestión de pacientes

## Documentación API (Swagger)

Una vez iniciada la aplicación, acceder a:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Monitoreo (Actuator)

Endpoints de monitoreo disponibles:
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`

## Logs

Los logs se generan en el directorio `./logs/`:
- `udipsai_backend.log` - Log actual
- `udipsai_backend_YYYY-MM-DD.log` - Logs por fecha
- Retención: 30 días

## Ventajas de la Consolidación

### ✅ Despliegue Simplificado
- Un solo servicio para desplegar
- Menos recursos de infraestructura
- Configuración centralizada

### ✅ Desarrollo Eficiente
- Código compartido más fácil de mantener
- Transacciones entre módulos simplificadas
- Debug más sencillo

### ✅ Mejor Rendimiento
- Sin latencia de red entre servicios
- Transacciones ACID completas
- Menos overhead de comunicación

### ✅ Mantenimiento Reducido
- Una sola base de código
- Versionado simplificado
- Testing integrado más fácil

## Migración desde Microservicios

Los microservicios originales se mantienen en:
- `backend/servicio_autentificacion_udipsai/`
- `backend/servicio_usuarios_udipsai/`
- `backend/servicio_citas_udipsai/`

Para volver a la arquitectura de microservicios si es necesario, los servicios originales siguen disponibles.

## Próximos Pasos

1. **Revisar configuraciones de datasource** para MySQL en el módulo de citas
2. **Consolidar clases de configuración** duplicadas (SecurityConfig, JwtUtil, etc.)
3. **Optimizar imports** y eliminar dependencias duplicadas
4. **Ejecutar tests** unitarios e integración
5. **Actualizar docker-compose.yml** para usar el backend unificado

## Contacto y Soporte

Para dudas o problemas:
- Revisar los logs en `./logs/`
- Verificar configuración de variables de entorno
- Consultar documentación de API en Swagger

---
**Versión:** 1.0.0  
**Última actualización:** Noviembre 2025
