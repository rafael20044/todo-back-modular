# Directrices de Arquitectura: Modular + Capas Pragmáticas + Value Objects

Este documento define los patrones estructurales y de diseño que rigen el proyecto Todo.

---

## 1. Arquitectura Modular (Módulos por Dominio)

El proyecto organiza el código en **módulos de negocio independientes (Vertical Slices)**, complementados por componentes transversales compartidos en `shared/` y configuraciones globales en `configuration/`:

```text
com.rafael.todo/
├── configuration/                      # Configuración global y variables de entorno
│   ├── ApplicationConfiguration.java   # Lectura centralizada de propiedades (application.yaml)
│   └── SecurityConfiguration.java      # Configuración de Spring Security y PasswordEncoder
│
├── shared/                             # Componentes compartidos y transversales
│   ├── DomainException.java            # Excepción base con code, message y statusCode
│   ├── ErrorDto.java                   # DTO de respuesta de error unificado
│   ├── GlobalExceptionController.java  # @RestControllerAdvice global para toda la app
│   ├── IJwtService.java                # Contrato compartido para emisión y validación de JWT
│   ├── JwtService.java                 # Implementación JWT con Auth0 java-jwt y HMAC256
│   └── MyConst.java                    # Enum centralizado de constantes (regex, longitudes, cookies)
│
└── module/                             # Módulos de funcionalidad (Vertical Slices)
    ├── auth/                           # Módulo de autenticación JWT y Refresh Tokens
    ├── user/                           # Módulo de usuarios (completado + tests)
    ├── category/                       # Módulo de categorías (completado + tests)
    ├── task/                           # Módulo de tareas (completado + tests)
    └── subtask/                        # Módulo de subtareas (completado + tests)
        ├── controller/                 # Capa de Presentación (Controladores REST, DTOs, mappers)
        ├── service/                    # Capa de Aplicación (Casos de uso / servicios, DTOs, mappers)
        ├── repository/                 # Capa de Persistencia (Spring Data JPA, entity/, adapter/, mapper/)
        ├── model/                      # Modelo de Dominio inmutable, vo/ (Value Objects) y enums/
        └── exception/                  # Fábricas de excepciones de dominio del módulo
```

### Reglas de Diseño Pragmático (Sin Hexagonal Estricta)
- **Simplicidad**: Se evita la sobreingeniería de crear múltiples interfaces de puertos por cada operación CRUD.
- **Aislamiento**: Cada módulo gestiona sus propias entidades, modelos y controladores.
- **Comunicación e Integridad Inter-Modular (Regla de Oro)**:
  - **Prohibido**: Relaciones JPA directas (`@ManyToOne`, `@OneToMany`) entre entidades de diferentes módulos (por ejemplo: `RefreshTokenEntity`, `CategoryEntity`, `TaskEntity` o `SubtaskEntity` jamás referencian directamente entidades de otros módulos).
  - **Persistencia por IDs Escalares**: Las tablas y entidades JPA solo almacenan el identificador escalar del módulo relacionado (`UUID userId`, `Long categoryId`, `Long taskId`).
  - **Validación en Capa de Servicio (Simple o Múltiple)**: Para evitar violaciones de clave foránea en base de datos (`DataIntegrityViolationException` / HTTP 500), el servicio del módulo inyecta las interfaces de los casos de uso necesarios (`AuthService` inyecta `IFindUserUseCase` e `IJwtService`; `CategoryService` inyecta `IFindUserUseCase`; `TaskService` inyecta `IFindUserUseCase` e `IFindCategoryUseCase`; `SubtaskService` inyecta `IFindTaskUseCase`) y valida la existencia antes de persistir (`findUserUseCase.findByEmail(...)`, `findUserUseCase.findById(...)`, `findCategoryUseCase.findById(...)`, `findTaskUseCase.findById(...)`), retornando códigos HTTP semánticos (`401`, `403`, `404`).
- **Aislamiento de Lógica, Rol de Mappers y Creación de Esqueletos**:
  - **Generación Exclusiva de Esqueletos para Implementaciones**: Al crear las clases que implementan interfaces o contratos —específicamente **servicios (`*Service`)**, **adaptadores (`*Adapter`)** y **controladores (`*Controller`)**— el asistente DEBE generar ÚNICAMENTE el esqueleto técnico (anotaciones, inyección de dependencias por constructor con `@Autowired`, firmas completas de métodos y cuerpos con `return null;` o vacíos). La lógica interna de estos métodos está reservada para el usuario o su solicitud explícita.
  - **Mappers Responsabilidad del Asistente**: Los mappers (`*MapperController`, `*MapperService`, `*MapperRepository`) son generados y completados en su totalidad por el asistente para asegurar la transformación estructural completa de datos entre capas (Entity $\leftrightarrow$ Model, Controller DTOs $\leftrightarrow$ Service DTOs/Model), sin introducir lógica de negocio ni bifurcaciones de reglas.
  - **Lógica de Negocio en Dominio y Servicio**: La lógica de negocio reside exclusivamente en `Model/vo` (invariantes y transiciones de estado) y en `Service` (orquestación). El asistente proporciona esqueletos y contratos limpios sin asumir o inventar reglas de negocio no especificadas por el usuario.

---

## 2. Capas Dentro de Cada Módulo

### A. Capa de Dominio (`model`, `enums`, `exception`)
- **Modelos Inmutables (`*Model.java`)**:
  - Clases `final` con todos los atributos `final`.
  - Constructor privado con `@AllArgsConstructor(access = AccessLevel.PRIVATE)`.
  - Métodos estáticos de fábrica:
    - `create(...)`: Para instanciar nuevos objetos (asigna timestamps actuales e `id = null` si es autoincremental).
    - `reconstitute(...)`: Para reconstruir el modelo a partir de los datos leídos de la persistencia sin alterar timestamps.
  - Métodos de negocio inmutables (*copy-on-write*): devuelven una nueva instancia mediante un método privado `copy(...)` (ej. `changeName`, `changePassword`, `revoke`).
  - Getters de conveniencia para primitivos (`getIdValue()`, `getNameValue()`, `isRevokedValue()`).

### B. Value Objects (`model/vo`)
- Implementados estrictamente con **`record`s de Java 25**.
- **Auto-validación (Invariantes)**: La validación se ejecuta en el constructor compacto. Es imposible instanciar un Value Object con estado inválido.
- Se apoyan en las constantes de [`MyConst`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/MyConst.java) y lanzan las excepciones específicas del paquete `exception/`.

### C. Excepciones de Dominio (`exception`)
- Clases `public final class ... extends DomainException`.
- Constructor privado que invoca `super(code, message, statusCode)`.
- Métodos estáticos de fábrica semánticos que asocian el código HTTP adecuado:
  - `400 Bad Request` para validaciones de formato, obligatoriedad o longitud.
  - `401 Unauthorized` para credenciales inválidas o refresh tokens expirados/revocados.
  - `403 Forbidden` para cuentas de usuario deshabilitadas (`accountDisabled`).
  - `404 Not Found` para recursos o IDs inexistentes.
  - `409 Conflict` para violaciones de unicidad (ej. correo o nombre de categoría duplicado).

### D. Capa de Persistencia (`repository`)
- **Interfaz del repositorio**: `I<Modulo>Repository` que extiende `JpaRepository`.
- **Filtros Dinámicos en JPQL**: Para consultas con parámetros opcionales simples o combinados con `AND` (ej. `userId`, `categoryId`, `status`, `isRevoked`), se utiliza la cláusula:
  ```sql
  @Query("SELECT e FROM Entity e WHERE (:param IS NULL OR e.field = :param)")
  ```
  resolviendo el filtrado en base de datos en una única consulta sin bifurcaciones en Java.
- **Operaciones de Eliminación Derivadas**: Los métodos `deleteBy...` personalizados en interfaces `JpaRepository` deben anotarse con `@Modifying` y `@Transactional`.
- **Subpaquete `entity/`**: Entidades JPA (`@Entity`, `@Table`) con tipos de datos de base de datos.
- **Subpaquete `adapter/`**: Adaptadores (`@Component`) que coordinan entre `I<Modulo>Repository`, `PasswordEncoder` (si aplica) y el mapeo hacia/desde el modelo de dominio. Las clases de implementación se generan inicialmente como esqueletos técnicos (`return null;`).
- **Subpaquete `mapper/`**: Mappers estáticos bidireccionales entre Entity y Model (`toEntity`, `toModel`).

### E. Capa de Aplicación (`service`)
- Interfaces de casos de uso (`ICreate...UseCase`, `ILoginUseCase`, etc.) implementadas en `...Service`. Las clases de implementación se generan inicialmente como esqueletos técnicos (`return null;`).
- Orquestan las reglas de negocio, validaciones cruzadas entre módulos y llamadas al adaptador de persistencia.
- **Cero Dependencias HTTP/Servlet**: La capa de servicio no debe importar clases de `jakarta.servlet.http.*` ni `org.springframework.http.ResponseCookie`.
- Subpaquetes: `dto/` (Records de servicio), `imp/` (Implementación con `@Log`) y `mapper/` (Mapper a modelo de dominio).

### F. Capa de Presentación (`controller`)
- Controladores `@RestController` con `@RequestMapping` y `@Log`. Generados inicialmente con esqueleto técnico (`return null;`).
- **Endpoint Unificado de Listados (`findAll`)**: Un único endpoint `GET` que recibe filtros opcionales como `@RequestParam` y paginación (`page`, `size`), retornando `ResponseEntity<Page<ResponseDto>>`.
- **Eliminación (`DELETE`)**: Siempre retorna `ResponseEntity<Void>` con status HTTP 204 (`ResponseEntity.noContent().build()`).
- **Gestión de Cookies `HttpOnly` (`AuthController`)**:
  - En `login` y `refresh`, el controlador recibe `AuthTokensDtoService` del servicio, adjunta el `refreshToken` en una cookie `HttpOnly` (`ResponseCookie` en cabecera `HttpHeaders.SET_COOKIE` con nombre `MyConst.COOKIE_NAME.getValue()`) y devuelve únicamente el `accessToken` en el cuerpo JSON (`ResponseAuthDto`).
  - En `logout`, el controlador recibe la cookie con `@CookieValue(name = "token")` y devuelve status 204 limpiando la cookie con `.maxAge(0)`.
- DTOs de entrada (`Create...Dto`, `Update...Dto`, `LoginDto`) y de salida (`Response...Dto`) implementados como `record`s.
- Mappers estáticos en subpaquete `mapper/`.

---

## 3. Manejo Global de Errores (`shared`)
- Toda excepción que herede de `DomainException` es capturada de forma automática por [`GlobalExceptionController`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/GlobalExceptionController.java) (`@RestControllerAdvice`).
- La respuesta HTTP devuelve un JSON estándar con [`ErrorDto`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/ErrorDto.java) (`code`, `message`, `status`), garantizando consistencia en todos los módulos de la aplicación.

---

## 4. Arquitectura de Pruebas Unitarias (`src/test/java`)
Cada módulo replica su estructura bajo `src/test/java/com/rafael/todo/module/<modulo>/` con 6 suites:
- `model/vo/*ValueObjectsTest.java`
- `model/*ModelTest.java`
- `mapper/*MappersTest.java`
- `service/*ServiceTest.java`
- `repository/*AdapterTest.java`
- `controller/*ControllerTest.java`
