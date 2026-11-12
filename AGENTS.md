# Reglas del Agente - Proyecto Todo (Spring Boot & Java 25)

Este archivo define las directrices maestras, arquitectura y flujo de trabajo para el desarrollo de este proyecto.

---

## 🚨 Regla Cardinal: Flujo Obligatorio de Trabajo (Plan Primero)
**Antes de realizar cualquier cambio, creación de código o refactorización, el asistente DEBE:**
1. Presentar un **Plan Detallado** al usuario explicando:
   - **Qué** se va a hacer (objetivo y alcance).
   - **Cómo** se va a hacer (archivos afectados, estructura de clases, patrones a usar).
   - Posibles implicaciones o alternativas consideradas.
2. **Esperar la confirmación o retroalimentación del usuario** antes de escribir o modificar archivos de código.
3. Proponer **sugerencias de mejora** proactivamente si se detectan optimizaciones de diseño, seguridad o rendimiento.

---

## 🏛️ Principios de Arquitectura

El proyecto adopta una **Arquitectura Modular por Capas Pragmática (Vertical Slices)**.  
*Nota: Se descarta la sobreingeniería de arquitectura hexagonal estricta por la simplicidad del proyecto.*

### 1. Organización Modular
- Cada contexto o funcionalidad de negocio se encapsula en su propio paquete:  
  `com.rafael.todo.module.<nombre_modulo>` (`auth`, `user`, `category`, `task`, `subtask`).
- Componentes transversales y compartidos residen en:  
  `com.rafael.todo.shared` (`DomainException`, `ErrorDto`, `GlobalExceptionController`, `MyConst`, `IJwtService`, `JwtService`).
- Configuración global (seguridad, propiedades de aplicación, beans globales) reside en:  
  `com.rafael.todo.configuration` (`SecurityConfiguration`, `ApplicationConfiguration`).

### 2. Capas por Módulo
Dentro de cada módulo se organiza una estructura de capas clara y directa:
1. **`controller`**: Controladores REST (`@RestController` con `@Log`), DTOs de petición/respuesta (`record`s) y mappers a servicios.
   - Endpoint unificado `findAll`: Soporta filtros opcionales por `@RequestParam` y paginación con `Page<ResponseDto>`.
   - `DELETE`: Retorna siempre `ResponseEntity<Void>` con status 204 (`noContent().build()`).
   - **Cookies `HttpOnly` (`auth`)**: El manejo de cookies (`ResponseCookie`, `HttpHeaders.SET_COOKIE`, `@CookieValue`) reside exclusivamente en el controlador para mantener la capa `service` desacoplada del protocolo HTTP.
2. **`service`**: Casos de uso / lógica de negocio, DTOs de servicio y mappers a modelos de dominio.
3. **`repository`**: Persistencia con Spring Data JPA. Contiene subpaquete `entity/` (entidades JPA), `adapter/` (adaptadores que conectan el repositorio con el servicio) y `mapper/` (mappers bidireccionales entre Entity y Model).
   - Filtros opcionales en `@Query` con la cláusula JPQL dinámica: `(:param IS NULL OR e.field = :param)` (soporta combinación multi-filtro con `AND`).
4. **`model`**: Modelos de dominio inmutables ricos y subpaquete **`vo/`** con Value Objects (`record`) y **`enums/`** (enums propios del módulo).
5. **`exception`**: Fábricas de excepciones de dominio que extienden de `DomainException`.

### 3. Regla de Oro: Relaciones y Validación Inter-Modular
- **Cero Acoplamiento JPA entre Módulos**: Prohibido el uso de relaciones directas `@ManyToOne` o `@OneToMany` entre entidades de diferentes módulos.
- **Persistencia por IDs Escalares**: Se almacenan únicamente los identificadores (`UUID userId`, `Long categoryId`, `Long taskId`).
- **Validación Vía Casos de Uso (Simple y Múltiple)**: La verificación de integridad referencial y consulta entre módulos se realiza en la capa de `Service` inyectando el o los casos de uso correspondientes (`AuthService` inyecta `IFindUserUseCase` e `IJwtService`; `CategoryService` inyecta `IFindUserUseCase`; `TaskService` inyecta `IFindUserUseCase` e `IFindCategoryUseCase`; `SubtaskService` inyecta `IFindTaskUseCase`), evitando excepciones de base de datos (`DataIntegrityViolationException`) y garantizando respuestas HTTP claras (`401`, `403`, `404`).

### 4. Regla de Aislamiento de Lógica, Rol de Mappers y Esqueletos Técnicos
- **Generación Exclusiva de Esqueletos para Implementaciones**:
  Al crear las clases que implementan interfaces o contratos —específicamente **servicios / casos de uso (`*Service`)**, **adaptadores (`*Adapter`)** y **controladores (`*Controller`)**— el asistente DEBE generar ÚNICAMENTE el esqueleto técnico:
  - Declaración de clase y anotaciones pertinentes (`@Service`, `@Component`, `@RestController`, `@Log`).
  - Inyección de dependencias por constructor con `@Autowired`.
  - Firmas completas de los métodos requeridos por las interfaces o rutas HTTP (`@PostMapping`, `@GetMapping`, etc.).
  - **Cuerpos de métodos vacíos o retornando `null;` (`return null;`)**.
  - Queda estrictamente prohibido que el asistente implemente la lógica interna de estos métodos a menos que el usuario lo solicite de manera explícita.
- **Mappers Responsabilidad del Asistente**: Los mappers (`*MapperController`, `*MapperService`, `*MapperRepository`) deben ser construidos y completados en su totalidad por el asistente, implementando la transformación estructural exhaustiva entre capas (Entity $\leftrightarrow$ Model, Controller DTOs $\leftrightarrow$ Service DTOs/Model), manteniéndose como transformaciones puras sin bifurcaciones de reglas de negocio.
- **Lógica de Negocio Reservada (Dominio y Servicio)**: La lógica y reglas de negocio residen únicamente en el **Modelo/Value Objects** (invariantes y transiciones de estado) y en el **`Service`** (orquestación). El asistente no debe inventar ni asumir reglas de negocio no solicitadas; debe proveer las firmas, contratos y esqueletos técnicos, dejando que el usuario defina o guíe la lógica exacta.

---

## 💎 Modelos de Dominio y Value Objects
- **Modelos Inmutables**: Clases `final` con atributos `final`, constructor privado, fábricas estáticas (`create` y `reconstitute`), métodos de negocio por copia (*copy-on-write*, ej. `changeName`, `revoke`) y getters de tipos primitivos (`getIdValue()`, etc.).
- **Value Objects**: Implementados con **`record`s de Java 25** para garantizar inmutabilidad, semántica por valor y validación de invariantes en constructores compactos.
- **Constantes Centralizadas**: Valores límite (mínimos, máximos, regex, nombres de cookies) se definen en el enum [`MyConst`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/MyConst.java).
- **Propiedades de Configuración Centralizadas**: Las variables definidas en `application.yaml` se inyectan y exponen a través de [`ApplicationConfiguration`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/configuration/ApplicationConfiguration.java).

---

## 🛡️ Manejo Centralizado de Excepciones
- Todas las excepciones de negocio extienden de [`DomainException(code, message, statusCode)`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/DomainException.java).
- El controlador global [`GlobalExceptionController`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/GlobalExceptionController.java) (`@RestControllerAdvice`) en `shared` intercepta automáticamente cualquier `DomainException` y responde con [`ErrorDto`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/java/com/rafael/todo/shared/ErrorDto.java) y el código HTTP correspondiente (400, 401, 403, 404, 409, 500).

---

## 🧪 Estrategia y Estándares de Testing (`src/test/java`)
Cada módulo en `src/test/java/com/rafael/todo/module/<modulo>/` cuenta con una suite estandarizada de **6 clases de pruebas unitarias**:
1. **`model/vo/*ValueObjectsTest.java`**: Pruebas con JUnit 5 (`@Nested`, `@DisplayName`, `@ParameterizedTest`) validando creación, normalización y excepciones de dominio (`400 Bad Request`).
2. **`model/*ModelTest.java`**: Pruebas de fábricas (`create`, `reconstitute`) y mutaciones inmutables *copy-on-write*.
3. **`mapper/*MappersTest.java`**: Pruebas exhaustivas de transformación para `*MapperController`, `*MapperService` y `*MapperRepository`.
4. **`service/*ServiceTest.java`**: Pruebas unitarias con Mockito (`@ExtendWith(MockitoExtension.class)`) para todos los casos de uso y validaciones inter-modulares (`401`/`403`/`404`/`409`).
5. **`repository/*AdapterTest.java`**: Pruebas unitarias con Mockito para el adaptador de persistencia (`I*Repository`, `PasswordEncoder`).
6. **`controller/*ControllerTest.java`**: Pruebas de endpoints REST utilizando `MockMvcBuilders.standaloneSetup` junto con `GlobalExceptionController`, verificando códigos HTTP (`200`, `201`, `204`, `400`, `401`, `403`, `404`, `409`), cabeceras (`Location`, `Set-Cookie`) y contratos JSON.

---

## 📖 Documentación Técnica del Proyecto (`/doc`)
La documentación oficial reside en [`doc/`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/doc) y debe mantenerse sincronizada ante nuevos módulos o cambios de diseño:
- [`doc/README.md`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/doc/README.md): Índice general, stack tecnológico y guía rápida.
- [`doc/architecture.md`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/doc/architecture.md): Arquitectura modular (*Vertical Slices*), flujo entre capas y manejo de errores.
- [`doc/database.md`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/doc/database.md): Esquema SQL en Flyway ([`V1__initial_schema.sql`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/src/main/resources/db/migration/V1__initial_schema.sql)), diagrama ER y diccionario de tablas.
- [`doc/api-reference.md`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/doc/api-reference.md): Catálogo completo de endpoints REST y payloads JSON.
- [`doc/domain-and-validations.md`](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/doc/domain-and-validations.md): Modelos inmutables, Value Objects, invariantes y constantes de `MyConst`.

---

## 🪵 Trazabilidad y Logs
- Uso de la anotación `@Log` de Lombok (`import lombok.extern.java.Log;`) en controladores, servicios, adaptadores y manejador global de excepciones para monitorear el flujo de la aplicación desde la terminal.

---

## 🚫 Restricción de Terminal
- Queda estrictamente prohibido ejecutar comandos de Gradle (`.\gradlew.bat compileJava`, `.\gradlew.bat test`, `.\gradlew.bat build`, `.\gradlew.bat bootRun`) desde la terminal del agente (tanto dentro como fuera del sandbox) para evitar bloqueos del entorno/daemon o políticas de seguridad. Toda verificación por parte del asistente se realiza mediante análisis estático de código.

---

## 📚 Documentos de Reglas Específicas
Para consultar directrices detalladas, revisa:
- [Arquitectura y Value Objects](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/.agents/rules/architecture.md)
- [Flujo de Trabajo y Planificación](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/.agents/rules/workflow.md)
- [Estándares Técnicos (Java 25 & Spring Boot)](file:///c:/Users/rafae/Documents/antigravity/todo-back-modular/.agents/rules/tech-stack.md)
