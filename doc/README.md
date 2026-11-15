# Todo Backend Modular (Spring Boot & Java 25)

API RESTful para la gestión de autenticación JWT, usuarios, categorías, tareas y subtareas, construida sobre **Java 25** y **Spring Boot 4.1.1** bajo una **Arquitectura Modular por Capas Pragmática (Vertical Slices)** con **Modelos de Dominio Ricos Inmutables** y **Value Objects**, lista para consumirse desde clientes **Angular** y **Flutter**.

---

## 📚 Índice de Documentación

| Documento | Descripción |
| :--- | :--- |
| **[1. Arquitectura y Diseño](./architecture.md)** | Organización modular (*Vertical Slices*), flujo de datos por capas, filtro JWT Stateless, CORS, OpenAPI 3, desacoplamiento inter-modular, manejo global de excepciones y trazabilidad. |
| **[2. Base de Datos y Migraciones](./database.md)** | Esquema relacional en H2 gestionado por Flyway, diagrama Entidad-Relación (ER), diccionario de tablas, índices y consola web H2. |
| **[3. Referencia de la API REST](./api-reference.md)** | Catálogo completo de endpoints (`/api/auth`, `/api/users`, `/api/categories`, `/api/tasks`, `/api/subtasks`), Swagger UI (`/api/swagger-ui/index.html`), rutas públicas vs. protegidas (`Bearer`), cookies `HttpOnly`, contratos JSON y errores. |
| **[4. Dominio, Value Objects y Validaciones](./domain-and-validations.md)** | Modelos inmutables (*copy-on-write*), Value Objects (`record`), Enums, invariantes de dominio y constantes centralizadas (`MyConst`). |

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
| :--- | :--- | :--- |
| **Java JDK** | `25` | Lenguaje base, `record`s para DTOs y Value Objects, inmutabilidad |
| **Spring Boot** | `4.1.1` | Framework principal de aplicación y contenedor IoC |
| **Spring Web MVC** | `4.1.1` | Exposición de controladores RESTful (`/api`) y cookies `HttpOnly` |
| **Spring Data JPA / Hibernate** | `7.4.5` | Capa de persistencia ORM y consultas dinámicas JPQL |
| **Spring Security** | `7.1.1` | Seguridad HTTP Stateless, CORS, `JwtAuthenticationFilter` y `BCryptPasswordEncoder` |
| **Auth0 Java JWT** | `4.5.0` | Generación y verificación de JSON Web Tokens (`HMAC256`) |
| **SpringDoc OpenAPI 3** | `3.2.0` | Documentación interactiva Swagger UI y especificación OpenAPI 3 |
| **Flyway** | Integrado | Control de versiones y migraciones SQL deterministas (`V1__initial_schema.sql`) |
| **H2 Database** | En memoria / Archivo | Motor de base de datos relacional rápido para desarrollo, pruebas y contenedor Docker |
| **Lombok** | Última | Reducción de código repetitivo (`@Getter`, `@AllArgsConstructor`, `@Log`) |
| **Docker & Compose** | Multi-stage | Empaquetado ligero con `eclipse-temurin:25-jre-alpine` y persistencia H2 en volumen |
| **Gradle (Kotlin DSL)** | Wrapper | Gestión de dependencias (`build.gradle.kts`) y automatización de compilación |

---

## 🚀 Guía Rápida de Inicio

### Opción A: Ejecución Local con Gradle
1. **Requisitos**: JDK 25 instalado y configurado en `JAVA_HOME`.
2. **Compilar el proyecto**:
   ```powershell
   .\gradlew.bat compileJava
   ```
3. **Ejecutar la aplicación**:
   ```powershell
   .\gradlew.bat bootRun
   ```
4. **Ejecutar la suite de pruebas (30 clases de test)**:
   ```powershell
   .\gradlew.bat test
   ```

### Opción B: Ejecución con Docker Compose
```powershell
docker compose up -d --build
```
Para detener los contenedores:
```powershell
docker compose down
```

### 🌐 URLs Disponibles al Iniciar
- **URL Base de la API**: `http://localhost:8080/api`
- **Swagger UI (Documentación Interactiva)**: `http://localhost:8080/api/swagger-ui/index.html`
- **Especificación OpenAPI 3 (JSON)**: `http://localhost:8080/api/v3/api-docs`
- **Consola Web H2**: `http://localhost:8080/api/h2-console`
  - **JDBC URL (Local)**: `jdbc:h2:mem:tododb` *(en Docker: `jdbc:h2:file:/app/data/tododb`)*
  - **User Name**: `sa`
  - **Password**: *(dejar vacío)*

---

## 🗂️ Estructura Raíz del Proyecto

```text
todo-back-modular/
├── .agents/                          # Reglas y directrices para agentes de IA
├── doc/                              # Documentación técnica del proyecto
│   ├── README.md                     # Índice general y guía rápida
│   ├── architecture.md               # Arquitectura modular y patrones
│   ├── database.md                   # Esquema SQL, ER y Flyway
│   ├── api-reference.md              # Endpoints REST, Swagger UI y ejemplos JSON
│   └── domain-and-validations.md     # Modelos, Value Objects y reglas de validación
├── src/
│   ├── main/
│   │   ├── java/com/rafael/todo/
│   │   │   ├── configuration/        # ApplicationConfiguration, JwtAuthenticationFilter, OpenApiConfiguration, SecurityConfiguration
│   │   │   ├── shared/               # DomainException, ErrorDto, GlobalExceptionController, IJwtService, JwtService, MyConst
│   │   │   ├── module/               # Módulos de negocio (auth, user, category, task, subtask)
│   │   │   └── TodoApplication.java  # Punto de entrada Spring Boot
│   │   └── resources/
│   │       ├── db/migration/         # Scripts SQL de Flyway (V1__initial_schema.sql)
│   │       └── application.yaml      # Configuración parametrizada del servidor, H2, JPA, Flyway, JWT y CORS
│   └── test/                         # Suites de pruebas unitarias por módulo (6 clases por módulo)
├── .dockerignore                     # Exclusiones de contexto para compilación en Docker
├── AGENTS.md                         # Reglas maestras del agente
├── build.gradle.kts                  # Configuración de construcción y dependencias
├── docker-compose.yaml               # Orquestación del contenedor con volumen persistente H2
└── Dockerfile                        # Construcción multi-stage (JDK 25 Alpine -> JRE 25 Alpine no-root)
```
