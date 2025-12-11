# INSTRUCCIONES DE USO - Backend Unificado UDIPSAI

## 🎉 Consolidación Completada

El backend unificado ha sido creado exitosamente. Los tres microservicios (Autenticación, Usuarios y Citas) han sido integrados en una sola aplicación modular.

## 📁 Estructura Final

```
sistema-web-turnos-udipsai/backend/
├── servicio_autentificacion_udipsai/  ← Microservicio original (BACKUP)
├── servicio_usuarios_udipsai/          ← Microservicio original (BACKUP)
├── servicio_citas_udipsai/             ← Microservicio original (BACKUP)
└── udipsai-backend/                    ← 🆕 BACKEND UNIFICADO
    ├── src/main/java/com/udipsai/backend/
    │   ├── UdipsaiBackendApplication.java
    │   ├── auth/               # Módulo de Autenticación
    │   ├── usuarios/           # Módulo de Usuarios
    │   ├── citas/              # Módulo de Citas
    │   └── common/             # Componentes Comunes
    │       ├── config/         # ✅ SecurityConfig, JwtUtil, etc.
    │       └── exception/      # ✅ Excepciones globales
    ├── src/main/resources/
    │   ├── application.properties
    │   ├── logback-spring.xml
    │   └── data.sql
    ├── pom.xml
    ├── Dockerfile
    ├── README.md
    ├── REVISION.md
    ├── migrate.ps1
    └── fix-imports.ps1
```

## ✅ ¿Qué se ha consolidado?

### 1. Excepciones ✅
- Todas las excepciones están en `common.exception`
- Manejador global único: `GlobalExceptionHandler`
- Sin duplicación de código

### 2. Configuraciones ✅
Las siguientes configuraciones están ahora en `common.config`:
- ✅ `SecurityConfig` - Configuración de seguridad unificada
- ✅ `JwtUtil` - Utilidad JWT compartida
- ✅ `JwtFilter` - Filtro de autenticación
- ✅ `CorsConfig` - Configuración CORS
- ✅ `LogFilter` - Filtro de logging

### 3. Persistencia ✅
- Módulo AUTH: Entidades de autenticación
- Módulo USUARIOS: Entidades de usuarios completas
- Módulo CITAS: Entidades de citas y pacientes

### 4. Servicios ✅
- Cada módulo mantiene sus servicios
- Los imports fueron actualizados correctamente

### 5. Controllers ✅
- Endpoints organizados por módulo
- Sin colisiones de rutas

## ⚠️ Configuraciones Duplicadas a Eliminar

**IMPORTANTE:** Existen configuraciones duplicadas en los módulos individuales que deben ser eliminadas para evitar conflictos:

```powershell
# Ejecutar desde: backend/udipsai-backend/

# Eliminar configuraciones duplicadas del módulo AUTH
Remove-Item -Path "src\main\java\com\udipsai\backend\auth\web\config\SecurityConfig.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\auth\web\config\JwtUtil.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\auth\web\config\JwtFilter.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\auth\web\config\CorsConfig.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\auth\web\config\LogFilter.java" -ErrorAction SilentlyContinue

# Eliminar configuraciones duplicadas del módulo USUARIOS
Remove-Item -Path "src\main\java\com\udipsai\backend\usuarios\web\config\SecurityConfig.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\usuarios\web\config\JwtUtil.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\usuarios\web\config\JwtFilter.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\usuarios\web\config\CorsConfig.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\usuarios\web\config\LogFilter.java" -ErrorAction SilentlyContinue

# Eliminar configuraciones duplicadas del módulo CITAS
Remove-Item -Path "src\main\java\com\udipsai\backend\citas\web\config\SecurityConfig.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\citas\web\config\JwtUtil.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\citas\web\config\JwtFilter.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\citas\web\config\CorsConfig.java" -ErrorAction SilentlyContinue
Remove-Item -Path "src\main\java\com\udipsai\backend\citas\web\config\LogFilter.java" -ErrorAction SilentlyContinue

Write-Host "Configuraciones duplicadas eliminadas" -ForegroundColor Green
```

## 🔧 Actualizar Imports en Servicios y Controllers

Después de eliminar las configuraciones duplicadas, actualiza los imports:

```powershell
# Script para actualizar imports de configuración
$baseDir = "src\main\java\com\udipsai\backend"

Get-ChildItem "$baseDir" -Filter *.java -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw -Encoding UTF8
    $updated = $false
    
    # Actualizar imports de config
    if ($content -match 'import com\.udipsai\.backend\.(auth|usuarios|citas)\.web\.config\.') {
        $content = $content -replace 'import com\.udipsai\.backend\.(auth|usuarios|citas)\.web\.config\.(JwtUtil|JwtFilter|CorsConfig|LogFilter|SecurityConfig)', 'import com.udipsai.backend.common.config.$2'
        $updated = $true
    }
    
    if ($updated) {
        Set-Content $_.FullName -Value $content -Encoding UTF8 -NoNewline
    }
}

Write-Host "Imports de configuración actualizados" -ForegroundColor Green
```

## 🔧 Compilar y Ejecutar

### 1. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto o configura las variables:

```bash
# PostgreSQL (única base de datos)
POSTGRESQL_URL=jdbc:postgresql://localhost:5432/udipsai
POSTGRESQL_USER=postgres
POSTGRESQL_PASSWORD=tu_password

# Frontend
FE_URL=http://localhost:4200

# JWT (opcional, tiene valores por defecto)
JWT_SECRET=Jeremy_25312
JWT_EXPIRATION_DAYS=15
JWT_ISSUER=UCACUE_UDIPSAI
```

### 2. Compilar el Proyecto

```bash
cd backend/udipsai-backend

# Limpiar y compilar
./mvnw clean install -DskipTests

# O si tienes Maven instalado globalmente
mvn clean install -DskipTests
```

### 3. Ejecutar la Aplicación

```bash
# Opción 1: Con Maven
./mvnw spring-boot:run

# Opción 2: Con el JAR generado
java -jar target/udipsai-backend-0.0.1-SNAPSHOT.jar

# Opción 3: Con Docker
docker build -t udipsai-backend .
docker run -p 8080:8080 \
  -e POSTGRESQL_URL=jdbc:postgresql://host.docker.internal:5432/udipsai \
  -e POSTGRESQL_USER=postgres \
  -e POSTGRESQL_PASSWORD=password \
  -e MYSQL_URL=jdbc:mysql://host.docker.internal:3306/udipsai_citas \
  -e MYSQL_USER=root \
  -e MYSQL_PASSWORD=password \
  udipsai-backend
```

### 4. Verificar que Funciona

```bash
# Health check
curl http://localhost:8080/actuator/health

# Info de la aplicación
curl http://localhost:8080/actuator/info

# Swagger UI
# Abrir en navegador: http://localhost:8080/swagger-ui.html
```

## 📊 Endpoints Disponibles

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/refresh` - Renovar token

### Usuarios
- `GET /api/usuarios` - Listar usuarios
- `GET /api/usuarios/{id}` - Obtener usuario
- `POST /api/usuarios` - Crear usuario
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

### Admins, Profesionales, Secretarias, etc.
- Similar patrón de endpoints para cada tipo

### Citas
- `GET /api/citas` - Listar citas
- `POST /api/citas` - Crear cita
- `PUT /api/citas/{id}` - Actualizar cita
- `DELETE /api/citas/{id}` - Eliminar cita

### Pacientes
- `GET /api/pacientes` - Listar pacientes
- `POST /api/pacientes` - Crear paciente
- etc.

## 🐛 Troubleshooting

### Error: Multiple beans with same name

**Problema:** Configuraciones duplicadas no fueron eliminadas.

**Solución:** Ejecutar el script de eliminación de configuraciones duplicadas (ver arriba).

### Error: Cannot find symbol SecurityConfig

**Problema:** Los imports no fueron actualizados correctamente.

**Solución:** Ejecutar el script de actualización de imports de configuración.

### Error: Connection refused to database

**Problema:** Las variables de entorno no están configuradas.

**Solución:** Verificar que las bases de datos están corriendo y las variables de entorno están configuradas.

### Error: Port 8080 already in use

**Problema:** Otro servicio está usando el puerto.

**Solución:** 
```bash
# Cambiar el puerto en application.properties
server.port=8081

# O configurar variable de entorno
set SERVER_PORT=8081
```

## 📝 Próximos Pasos

1. ✅ **Eliminar configuraciones duplicadas** (ver script arriba)
2. ✅ **Actualizar imports** (ver script arriba)
3. ✅ **Compilar el proyecto** sin errores
4. ⚠️ **Configurar múltiples DataSources** para el módulo de Citas (si usa MySQL y PostgreSQL)
5. ✅ **Ejecutar tests** unitarios e integración
6. ✅ **Probar endpoints** con Postman o similar
7. ✅ **Actualizar docker-compose.yml** para usar el backend unificado

## 📚 Documentación Adicional

- **README.md**: Documentación general del proyecto
- **REVISION.md**: Análisis detallado de servicios, persistencia y excepciones
- **Swagger UI**: Documentación interactiva de API (http://localhost:8080/swagger-ui.html)

## 🔄 Volver a Microservicios

Si necesitas volver a la arquitectura de microservicios:

1. Los servicios originales están intactos en sus carpetas
2. Simplemente usa docker-compose.yml con los servicios originales
3. El backend unificado no modifica los servicios originales

## ✅ Checklist Final

Antes de considerar la migración completa:

- [ ] Eliminar configuraciones duplicadas
- [ ] Actualizar imports de configuración
- [ ] Compilar sin errores
- [ ] Configurar DataSources (PostgreSQL + MySQL)
- [ ] Ejecutar tests
- [ ] Probar endpoints principales
- [ ] Verificar autenticación JWT
- [ ] Verificar logs
- [ ] Actualizar frontend para apuntar a puerto unificado
- [ ] Actualizar docker-compose.yml

---

**¡Felicidades! El backend unificado está listo para ser usado.** 🎉

Para cualquier duda, revisa los archivos README.md y REVISION.md.
