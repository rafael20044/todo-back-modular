# =============================================================================
# Etapa 1: Construcción (Build Stage) con JDK 25
# =============================================================================
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

# Copiar el wrapper de Gradle y archivos de configuración para cachear dependencias
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

# Asegurar permisos de ejecución y normalizar saltos de línea (CRLF -> LF en Windows)
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Descargar dependencias en una capa cacheable
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente y compilar el JAR ejecutable omitiendo los tests
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# =============================================================================
# Etapa 2: Ejecución (Runtime Stage) con JRE 25 ligero
# =============================================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# Crear usuario sin privilegios de root y directorio para datos persistentes de H2
RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir -p /app/data && chown -R spring:spring /app

# Copiar el artefacto compilado desde la etapa builder
COPY --from=builder --chown=spring:spring /workspace/build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
