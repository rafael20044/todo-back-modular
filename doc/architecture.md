# Arquitectura del Proyecto: Modular por Capas Pragmática

El proyecto implementa una **Arquitectura Modular por Capas Pragmática (Vertical Slices)** combinada con **Modelos de Dominio Ricos Inmutables** y **Value Objects**. Este enfoque ofrece alta cohesión funcional, bajo acoplamiento entre dominios y simplicidad operativa sin caer en la sobreingeniería de una arquitectura hexagonal estricta.

---

## 1. Organización Modular (Vertical Slices)

Cada contexto de negocio vive en su propio paquete bajo `com.rafael.todo.module.<nombre_modulo>`, manteniendo sus propias capas aisladas del resto de los módulos:

```mermaid
flowchart TD
    subgraph App["com.rafael.todo"]
        Config["configuration\n(SecurityConfiguration)"]
        Shared["shared\n(DomainException, ErrorDto,\nGlobalExceptionController, MyConst)"]
        
        subgraph Modules["module (Vertical Slices)"]
            User["user\n(Usuarios)"]
            Category["category\n(Categorías)"]
            Task["task\n(Tareas)"]
            Subtask["subtask\n(Subtareas)"]
        end
    end

    Subtask -->|Valida existencia vía UseCase| Task
    Task -->|Valida existencia vía UseCase| User
    Task -->|Valida existencia vía UseCase| Category
    Category -->|Valida existencia vía UseCase| User

    User -.-> Shared
    Category -.-> Shared
    Task -.-> Shared
    Subtask -.-> Shared
```

---

## 2. Estructura Interna de Cada Módulo

Cada módulo (`user`, `category`, `task`, `subtask`) respeta una estructura uniforme de paquetes:

```text
module/<nombre_modulo>/
├── controller/                   # Capa de Presentación REST
│   ├── dto/                      # Records de entrada (Create*Dto, Update*Dto) y salida (Response*Dto)
│   ├── mapper/                   # Transformación estática Controller DTO <-> Service DTO / Model
│   └── *Controller.java          # Endpoints HTTP (@RestController)
├── service/                      # Capa de Aplicación / Casos de Uso
│   ├── dto/                      # Records de transporte para casos de uso
│   ├── imp/                      # Implementación de servicios (*Service.java)
│   ├── mapper/                   # Transformación estática Service DTO -> Model
│   ├── ICreate*UseCase.java      # Contrato de creación
│   ├── IFind*UseCase.java        # Contrato de consulta (usado también inter-módulos)
│   ├── IUpdate*UseCase.java      # Contrato de actualización
│   └── IDelete*UseCase.java      # Contrato de eliminación
├── repository/                   # Capa de Persistencia
│   ├── adapter/                  # Contratos de adaptador e implementación (*AdapterRepository.java)
│   ├── entity/                   # Entidades JPA (@Entity, @Table)
│   ├── mapper/                   # Transformación bidireccional Entity <-> Model
│   └── I*Repository.java         # Interfaz Spring Data JPA (JpaRepository)
├── model/                        # Capa de Dominio
│   ├── enums/                    # Enumeradores propios del dominio (si aplica)
│   ├── vo/                       # Value Objects inmutables (Java records)
│   └── *Model.java               # Modelo de dominio rico e inmutable
└── exception/                    # Fábricas de excepciones semánticas del módulo
```

---

## 3. Flujo de una Petición entre Capas

```mermaid
sequenceDiagram
    autonumber
    actor Client as Cliente HTTP
    participant Ctrl as Controller
    participant Svc as Service (UseCase)
    participant Cross as Otro Módulo (IFind*UseCase)
    participant Dom as Model & Value Objects
    participant Adp as AdapterRepository
    participant JPA as I*Repository (Spring Data)

    Client->>Ctrl: HTTP Request (Create/Update DTO)
    Ctrl->>Ctrl: MapperController.toServiceDto(dto)
    Ctrl->>Svc: Invoca caso de uso
    opt Validación Inter-Modular
        Svc->>Cross: findById(foreignKeyId)
        Cross-->>Svc: Confirma existencia (o lanza 404 DomainException)
    end
    Svc->>Dom: MapperService -> Instancia VOs (valida invariantes) y Model
    Dom-->>Svc: Retorna *Model inmutable válido
    Svc->>Adp: Delega persistencia con *Model
    Adp->>Adp: MapperRepository.toEntity(model)
    Adp->>JPA: save(entity) / query
    JPA-->>Adp: Entity persistida
    Adp->>Adp: MapperRepository.toModel(entity)
    Adp-->>Svc: Retorna *Model reconstituido
    Svc-->>Ctrl: Retorna *Model
    Ctrl->>Ctrl: MapperController.toResponseDto(model)
    Ctrl-->>Client: HTTP Response (200 OK / 201 Created / 204 No Content)
```

---

## 4. Reglas Arquitectónicas Fundamentales

### A. Regla de Oro: Cero Acoplamiento JPA Inter-Modular
1. **Prohibición de Relaciones JPA entre Módulos**: Ninguna entidad JPA (`CategoryEntity`, `TaskEntity`, `SubtaskEntity`) utiliza `@ManyToOne` o `@OneToMany` apuntando a entidades de otro módulo.
2. **Persistencia por IDs Escalares**: Las entidades almacenan únicamente el identificador escalar (`UUID userId`, `Long categoryId`, `Long taskId`).
3. **Validación de Integridad Referencial en `Service`**:
   - `CategoryService` inyecta `IFindUserUseCase` para verificar que el usuario exista antes de crear una categoría.
   - `TaskService` inyecta `IFindUserUseCase` e `IFindCategoryUseCase` para validar la existencia del usuario y la categoría (si se envía).
   - `SubtaskService` inyecta `IFindTaskUseCase` para validar la existencia de la tarea padre.
   - Si el recurso relacionado no existe, el caso de uso lanza una `DomainException` con código **HTTP 404 Not Found**, evitando errores `DataIntegrityViolationException` (HTTP 500) en la base de datos.

### B. Aislamiento de Lógica de Negocio y Rol de Mappers
- **`controller`**: Exclusivamente recibe peticiones HTTP, delega al caso de uso y devuelve `ResponseEntity`. Cero reglas de negocio.
- **`repository / adapter`**: Exclusivamente coordina con Spring Data JPA, codifica contraseñas cuando aplica (`UserAdapterRepository`) y transforma `Entity <-> Model`.
- **`Mappers`**: Clases utilitarias con métodos estáticos puros (`*MapperController`, `*MapperService`, `*MapperRepository`) que garantizan la conversión exhaustiva entre capas sin lógica condicional de negocio.
- **`model / vo` y `service`**: Únicos lugares donde reside la lógica de negocio (invariantes de creación, transiciones de estado *copy-on-write* y orquestación de casos de uso).

---

## 5. Manejo Centralizado de Excepciones (`shared`)

Todas las excepciones de negocio heredan de `DomainException`:

```java
public class DomainException extends RuntimeException {
    private final String code;
    private final int statusCode;
    // ...
}
```

El componente [`GlobalExceptionController`](../src/main/java/com/rafael/todo/shared/GlobalExceptionController.java) (`@RestControllerAdvice`) intercepta automáticamente cualquier excepción y construye un [`ErrorDto`](../src/main/java/com/rafael/todo/shared/ErrorDto.java) uniforme:

| Tipo de Excepción | Código HTTP | Ejemplo de `code` | Cuándo ocurre |
| :--- | :---: | :--- | :--- |
| Validación de Value Object | `400 Bad Request` | `user.email.invalid`, `task.title.too.short` | Formato inválido, nulo o fuera de rango |
| Recurso Inexistente | `404 Not Found` | `user.id.not.found`, `task.id.not.found` | El ID buscado o referenciado no existe |
| Conflicto de Unicidad | `409 Conflict` | `user.email.duplicate`, `category.name.duplicate` | Registro duplicado (ej. email o categoría por usuario) |
| Error Inesperado | `500 Internal Server Error` | `internal.server.error` | Fallo no controlado en tiempo de ejecución |

**Formato JSON de respuesta de error (`ErrorDto`):**
```json
{
  "code": "user.id.not.found",
  "message": "El usuario con el id '...' no fue encontrado",
  "status": 404
}
```

---

## 6. Seguridad y Trazabilidad

### Seguridad (`SecurityConfiguration`)
Configurada en [`SecurityConfiguration.java`](../src/main/java/com/rafael/todo/configuration/SecurityConfiguration.java):
- Expone el bean `PasswordEncoder` con `BCryptPasswordEncoder` para el hash seguro de contraseñas de usuario.
- En modo desarrollo actual: deshabilita CSRF, permite todas las rutas bajo `/api/**` y habilita `frameOptions(sameOrigin)` para el uso de la consola H2.

### Trazabilidad (`@Log`)
Se utiliza `@Log` de Lombok (`java.util.logging.Logger`) a través de las capas para registrar:
- **`INFO`**: Entrada y salida exitosa de peticiones REST, inicio de casos de uso y operaciones de persistencia.
- **`WARNING`**: Excepciones de dominio controladas (400, 404, 409) y recursos no encontrados.
- **`SEVERE`**: Excepciones no controladas (500) en `GlobalExceptionController`.

