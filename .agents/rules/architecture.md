# Directrices de Arquitectura: Modular + Capas Pragmáticas + Value Objects

Este documento define los patrones estructurales y de diseño que rigen el proyecto Todo.

---

## 1. Arquitectura Modular (Módulos por Dominio)

El proyecto organiza el código en **módulos de negocio independientes (Vertical Slices)**, complementados por componentes transversales compartidos en `shared/`:

```
com.rafael.todo/
├── configuration/                # Configuración global (ej. SecurityConfiguration)
│
├── shared/                       # Componentes compartidos y transversales
│   ├── DomainException.java      # Excepción base con code, message y statusCode
│   ├── ErrorDto.java             # DTO de respuesta de error unificado
│   ├── GlobalExceptionController.java # @RestControllerAdvice global para toda la app
│   └── MyConst.java              # Enum centralizado de constantes (regex, longitudes)
│
└── module/                       # Módulos de funcionalidad (Vertical Slices)
    ├── user/                     # Módulo de usuarios
    ├── category/                 # Módulo de categorías
    ├── task/                     # Módulo de tareas
    └── subtask/                  # Módulo de subtareas (futuro)
        ├── controller/           # Capa de Presentación (Controladores REST, DTOs, mappers)
        ├── service/              # Capa de Aplicación (Casos de uso / servicios, DTOs, mappers)
        ├── repository/           # Capa de Persistencia (Spring Data JPA, entity/ y adapter/)
        ├── model/                # Modelo de Dominio inmutable y vo/ (Value Objects)
        ├── enums/                # Enumeradores propios del módulo
        └── exception/            # Fábricas de excepciones de dominio del módulo
```

### Reglas de Diseño Pragmático (Sin Hexagonal Estricta)
- **Simplicidad**: Se evita la sobreingeniería de crear múltiples interfaces de puertos por cada operación CRUD.
- **Aislamiento**: Cada módulo gestiona sus propias entidades, modelos y controladores.
- **Comunicación e Integridad Inter-Modular (Regla de Oro)**:
  - **Prohibido**: Relaciones JPA directas (`@ManyToOne`, `@OneToMany`) entre entidades de diferentes módulos (por ejemplo: `CategoryEntity` o `TaskEntity` jamás referencian directamente a `UserEntity`).
  - **Persistencia por IDs Escalares**: Las tablas y entidades JPA solo almacenan el identificador escalar del módulo relacionado (`UUID userId`, `Long categoryId`).
  - **Validación en Capa de Servicio (Simple o Múltiple)**: Para evitar violaciones de clave foránea en base de datos (`DataIntegrityViolationException` / HTTP 500), el servicio del módulo inyecta las interfaces de los casos de uso necesarios (ej. `TaskService` inyecta `IFindUserUseCase` e `IFindCategoryUseCase`) y valida la existencia antes de persistir (`findUserUseCase.findById(...)`, `findCategoryUseCase.findById(...)`), retornando un HTTP 404 claro si el recurso no existe.
- **Aislamiento de Lógica de Negocio y Rol de Mappers**:
  - **Prohibido en `controller` y `repository/adapter`**: Ambas capas periféricas deben estar estrictamente libres de lógica o reglas de decisión de negocio.
  - **Mappers Responsabilidad del Asistente**: Los mappers (`*MapperController`, `*MapperService`, `*MapperRepository`) son generados y completados en su totalidad por el asistente para asegurar la transformación estructural completa de datos entre capas (Entity $\leftrightarrow$ Model, Controller DTOs $\leftrightarrow$ Service DTOs/Model), sin introducir lógica de negocio.
  - **Lógica de Negocio en Dominio y Servicio**: La lógica de negocio reside exclusivamente en `Model/vo` (invariantes y transiciones) y `Service` (orquestación). El asistente proporciona esqueletos y contratos limpios sin asumir o inventar reglas de negocio no especificadas por el usuario.

---

## 2. Capas Dentro de Cada Módulo

### A. Capa de Dominio (`model`, `enums`, `exception`)
- **Modelos Inmutables (`Model.java`)**:
  - Clases `final` con todos los atributos `final`.
  - Constructor privado con `@AllArgsConstructor(access = AccessLevel.PRIVATE)`.
  - Métodos estáticos de fábrica:
    - `create(...)`: Para instanciar nuevos objetos (asigna timestamps actuales e `id = null` si es autoincremental).
    - `reconstitute(...)`: Para reconstruir el modelo a partir de los datos leídos de la persistencia sin alterar timestamps.
  - Métodos de negocio inmutables (*copy-on-write*): devuelven una nueva instancia mediante un método privado `copy(...)` (ej. `changeName`, `changePassword`).
  - Getters de conveniencia para primitivos (`getIdValue()`, `getNameValue()`).

### B. Value Objects (`model/vo`)
- Implementados estrictamente con **`record`s de Java 25**.
- **Auto-validación (Invariantes)**: La validación se ejecuta en el constructor compacto. Es imposible instanciar un Value Object con estado inválido.
- Se apoyan en las constantes de [`MyConst`](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/src/main/java/com/rafael/todo/shared/MyConst.java) y lanzan las excepciones específicas del paquete `exception/`.

### C. Excepciones de Dominio (`exception`)
- Clases `public final class ... extends DomainException`.
- Constructor privado que invoca `super(code, message, statusCode)`.
- Métodos estáticos de fábrica semánticos que asocian el código HTTP adecuado:
  - `400 Bad Request` para validaciones de formato, obligatoriedad o longitud.
  - `404 Not Found` para recursos o IDs inexistentes.
  - `409 Conflict` para violaciones de unicidad (ej. correo o nombre de categoría duplicado).

### D. Capa de Persistencia (`repository`)
- **Interfaz del repositorio**: `I<Modulo>Repository` que extiende `JpaRepository`.
- **Filtros Dinámicos en JPQL**: Para consultas con parámetros opcionales simples o combinados con `AND` (ej. `userId`, `categoryId`, `status`), se utiliza la cláusula:
  ```sql
  @Query("SELECT e FROM Entity e WHERE (:param IS NULL OR e.field = :param)")
  ```
  resolviendo el filtrado en base de datos en una única consulta sin bifurcaciones en Java.
- **Subpaquete `entity/`**: Entidades JPA (`@Entity`, `@Table`) con tipos de datos de base de datos.
- **Subpaquete `adapter/`**: Adaptadores (`@Component`) que coordinan entre `I<Modulo>Repository`, `PasswordEncoder` (si aplica) y el mapeo hacia/desde el modelo de dominio.
- **Subpaquete `mapper/`**: Mappers estáticos bidireccionales entre Entity y Model (`toEntity`, `toModel`).

### E. Capa de Aplicación (`service`)
- Interfaces de casos de uso (`ICreate...UseCase`, etc.) implementadas en `...Service`.
- Orquestan las reglas de negocio, validaciones cruzadas entre módulos y llamadas al adaptador de persistencia.
- Subpaquetes: `dto/` (Records de servicio), `imp/` (Implementación con `@Log`) y `mapper/` (Mapper a modelo de dominio).

### F. Capa de Presentación (`controller`)
- Controladores `@RestController` con `@RequestMapping` y `@Log`.
- **Endpoint Unificado de Listados (`findAll`)**: Un único endpoint `GET` que recibe filtros opcionales como `@RequestParam` y paginación (`page`, `size`), retornando `ResponseEntity<Page<ResponseDto>>`.
- **Eliminación (`DELETE`)**: Siempre retorna `ResponseEntity<Void>` con status HTTP 204 (`ResponseEntity.noContent().build()`).
- DTOs de entrada (`Create...Dto`, `Update...Dto`) y de salida (`Response...Dto`) implementados como `record`s.
- Mappers estáticos en subpaquete `mapper/`.

---

## 3. Manejo Global de Errores (`shared`)
- Toda excepción que herede de `DomainException` es capturada de forma automática por `GlobalExceptionController` (`@RestControllerAdvice`).
- La respuesta HTTP devuelve un JSON estándar con [`ErrorDto`](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/src/main/java/com/rafael/todo/shared/ErrorDto.java) (`code`, `message`, `status`), garantizando consistencia en todos los módulos de la aplicación.

