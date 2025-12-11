# ==========================================
# Dockerfile para UDIPSAI Backend Unificado
# ==========================================

# Primera etapa: Compilación con Maven
FROM maven:3.8.4-openjdk-17 as build-stage
WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar la aplicación sin ejecutar tests
RUN mvn clean package -DskipTests

# Segunda etapa: Imagen para ejecución
FROM openjdk:17-jdk-alpine
WORKDIR /backend

# Crear directorio para logs
RUN mkdir -p /backend/logs

# Copiar el JAR compilado desde la etapa anterior
COPY --from=build-stage /app/target/udipsai-backend-0.0.1-SNAPSHOT.jar /backend/udipsai-backend.jar

# Exponer puerto (configurable con variable de entorno)
EXPOSE 8080

# Variables de entorno por defecto (pueden ser sobrescritas)
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Punto de entrada de la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar udipsai-backend.jar"]
