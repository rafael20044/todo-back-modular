# Todo Backend Modular (Spring Boot & Java 25)

API RESTful para la gestión de usuarios, categorías, tareas y subtareas, construida sobre **Java 25** y **Spring Boot 4.1.1** bajo una **Arquitectura Modular por Capas Pragmática (Vertical Slices)** con **Modelos de Dominio Ricos Inmutables** y **Value Objects**.

---

## 📚 Índice de Documentación

| Documento | Descripción |
| :--- | :--- |
| **[1. Arquitectura y Diseño](./architecture.md)** | Organización modular (*Vertical Slices*), flujo de datos por capas, desacoplamiento inter-modular, manejo global de excepciones y trazabilidad. |
| **[2. Base de Datos y Migraciones](./database.md)** | Esquema relacional en H2 gestionado por Flyway, diagrama Entidad-Relación (ER), diccionario de tablas, índices y consola web H2. |
| **[3. Referencia de la API REST](./api-reference.md)** | Catálogo completo de endpoints (`/api/users`, `/api/categories`, `/api/tasks`, `/api/subtasks`), contratos JSON, paginación y respuestas de error. |
| **[4. Dominio, Value Objects y Validaciones](./domain-and-validations.md)** | Modelos inmutables (*copy-on-write*), Value Objects (`record`), Enums, invariantes de dominio y constantes centralizadas (`MyConst`). |

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
| :--- | :--- | :--- |
| **Java JDK** | `25` | Lenguaje base, `record`s para DTOs y Value Objects, inmutabilidad |
| **Spring Boot** | `4.1.1` | Framework principal de aplicación y contenedor IoC |
| **Spring Web MVC** | `4.1.1` | Exposición de controladores RESTful (`/api`) |
| **Spring Data JPA / Hibernate** | `7.4.5` | Capa de persistencia ORM y consultas dinámicas JPQL |
| **Spring Security** | `7.1.1` | Configuración de seguridad HTTP y cifrado de contraseñas (`BCryptPasswordEncoder`) |
| **Flyway** | Integrado | Control de versiones y migraciones SQL deterministas (`V1__initial_schema.sql`) |
| **H2 Database** | En memoria | Motor de base de datos relacional rápido para desarrollo y pruebas |
| **Lombok** | Última | Reducción de código repetitivo (`@Getter`, `@AllArgsConstructor`, `@Log`) |
| **Gradle** | Wrapper | Gestión de dependencias y automatización de compilación |

---

## 🚀 Guía Rápida de Inicio

### 1. Requisitos Previos
- **JDK 25** instalado y configurado en `JAVA_HOME`.
- **Gradle Wrapper** (incluido en el repositorio: `./gradlew` o `gradlew.bat`).

### 2. Compilar el Proyecto
```powershell
.\gradlew.bat compileJava
```

### 3. Ejecutar la Aplicación
```powershell
.\gradlew.bat bootRun
```
Una vez iniciada, la aplicación estará disponible en:
- **URL Base de la API**: `http://localhost:8080/api`
- **Consola Web H2**: `http://localhost:8080/api/h2-console`
  - **JDBC URL**: `jdbc:h2:mem:tododb`
  - **User Name**: `sa`
  - **Password**: *(dejar vacío)*

### 4. Ejecutar Pruebas
```powershell
.\gradlew.bat test
```

---

## 🗂️ Estructura Raíz del Proyecto

```text
todo-back-modular/
├── .agents/                          # Reglas y directrices para agentes de IA
├── doc/                              # Documentación técnica del proyecto
│   ├── README.md                     # Índice general y guía rápida
│   ├── architecture.md               # Arquitectura modular y patrones
│   ├── database.md                   # Esquema SQL, ER y Flyway
│   ├── api-reference.md              # Endpoints REST y ejemplos JSON
│   └── domain-and-validations.md     # Modelos, Value Objects y reglas de validación
├── src/
│   ├── main/
│   │   ├── java/com/rafael/todo/
│   │   │   ├── configuration/        # Configuración global (SecurityConfiguration)
│   │   │   ├── shared/               # Excepciones base, ErrorDto, GlobalExceptionController, MyConst
│   │   │   ├── module/               # Módulos de negocio (user, category, task, subtask)
│   │   │   └── TodoApplication.java  # Punto de entrada Spring Boot
│   │   └── resources/
│   │       ├── db/migration/         # Scripts SQL de Flyway (V1__initial_schema.sql)
│   │       └── application.yaml      # Configuración del servidor, H2, JPA y Flyway
│   └── test/                         # Pruebas automatizadas
└── build.gradle                      # Configuración de construcción y dependencias
```

