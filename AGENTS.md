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
  `com.rafael.todo.module.<nombre_modulo>` (`user`, `category`, `task`, `subtask` completados).
- Componentes transversales y compartidos residen en:  
  `com.rafael.todo.shared` (`DomainException`, `ErrorDto`, `GlobalExceptionController`, `MyConst`).
- Configuración global (seguridad, beans globales) reside en:  
  `com.rafael.todo.configuration`.

### 2. Capas por Módulo
Dentro de cada módulo se organiza una estructura de capas clara y directa:
1. **`controller`**: Controladores REST (`@RestController` con `@Log`), DTOs de petición/respuesta (`record`s) y mappers a servicios.
   - Endpoint unificado `findAll`: Soporta filtros opcionales por `@RequestParam` y paginación con `Page<ResponseDto>`.
   - `DELETE`: Retorna siempre `ResponseEntity<Void>` con status 204 (`noContent().build()`).
2. **`service`**: Casos de uso / lógica de negocio, DTOs de servicio y mappers a modelos de dominio.
3. **`repository`**: Persistencia con Spring Data JPA. Contiene subpaquete `entity/` (entidades JPA), `adapter/` (adaptadores que conectan el repositorio con el servicio) y `mapper/` (mappers bidireccionales entre Entity y Model).
   - Filtros opcionales en `@Query` con la cláusula JPQL dinámica: `(:param IS NULL OR e.field = :param)` (soporta combinación multi-filtro con `AND`).
4. **`model`**: Modelos de dominio inmutables ricos y subpaquete **`vo/`** con Value Objects.
5. **`enums`**: Enums propios del dominio del módulo.
6. **`exception`**: Fábricas de excepciones de dominio que extienden de `DomainException`.

### 3. Regla de Oro: Relaciones y Validación Inter-Modular
- **Cero Acoplamiento JPA entre Módulos**: Prohibido el uso de relaciones directas `@ManyToOne` o `@OneToMany` entre entidades de diferentes módulos.
- **Persistencia por IDs Escalares**: Se almacenan únicamente los identificadores (`UUID userId`, `Long categoryId`, `Long taskId`).
- **Validación Vía Casos de Uso (Simple y Múltiple)**: La verificación de integridad referencial entre módulos se realiza en la capa de `Service` inyectando el o los casos de uso correspondientes (ej. `TaskService` inyecta `IFindUserUseCase` e `IFindCategoryUseCase`; `SubtaskService` inyecta `IFindTaskUseCase`), evitando excepciones de base de datos (`DataIntegrityViolationException`) y garantizando respuestas HTTP 404 limpias.

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
- **Modelos Inmutables**: Clases `final` con atributos `final`, constructor privado, fábricas estáticas (`create` y `reconstitute`), métodos de negocio por copia (*copy-on-write*, ej. `changeName`) y getters de tipos primitivos (`getIdValue()`, etc.).
- **Value Objects**: Implementados con **`record`s de Java 25** para garantizar inmutabilidad, semántica por valor y validación de invariantes en constructores compactos.
- **Constantes Centralizadas**: Valores límite (mínimos, máximos, regex) se definen en el enum [`MyConst`](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/src/main/java/com/rafael/todo/shared/MyConst.java).

---

## 🛡️ Manejo Centralizado de Excepciones
- Todas las excepciones de negocio extienden de [`DomainException(code, message, statusCode)`](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/src/main/java/com/rafael/todo/shared/DomainException.java).
- El controlador global [`GlobalExceptionController`](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/src/main/java/com/rafael/todo/shared/GlobalExceptionController.java) (`@RestControllerAdvice`) en `shared` intercepta automáticamente cualquier `DomainException` y responde con [`ErrorDto`](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/src/main/java/com/rafael/todo/shared/ErrorDto.java) y el código HTTP correspondiente (400, 404, 409, 500).

---

## 🪵 Trazabilidad y Logs
- Uso de la anotación `@Log` de Lombok (`import lombok.extern.java.Log;`) en controladores, servicios, adaptadores y manejador global de excepciones para monitorear el flujo de la aplicación desde la terminal.

---

## 🚫 Restricción de Terminal
- Queda estrictamente prohibido ejecutar `.\gradlew.bat compileJava` o comandos de compilación continua de Gradle desde la terminal para evitar bloqueos del entorno/daemon. Toda verificación se realiza mediante análisis estático de código.

---

## 📚 Documentos de Reglas Específicas
Para consultar directrices detalladas, revisa:
- [Arquitectura y Value Objects](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/.agents/rules/architecture.md)
- [Flujo de Trabajo y Planificación](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/.agents/rules/workflow.md)
- [Estándares Técnicos (Java 25 & Spring Boot)](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/.agents/rules/tech-stack.md)
