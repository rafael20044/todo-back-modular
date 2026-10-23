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
  `com.rafael.todo.module.<nombre_modulo>` (por ejemplo: `user`, `category`, `task`).
- Componentes transversales y compartidos residen en:  
  `com.rafael.todo.shared` (`DomainException`, `ErrorDto`, `GlobalExceptionController`, `MyConst`).
- Configuración global (seguridad, beans globales) reside en:  
  `com.rafael.todo.configuration`.

### 2. Capas por Módulo
Dentro de cada módulo se organiza una estructura de capas clara y directa:
1. **`controller`**: Controladores REST (`@RestController`), DTOs de petición/respuesta y mappers a servicios.
2. **`service`**: Casos de uso / lógica de negocio, DTOs de servicio y mappers a modelos de dominio.
3. **`repository`**: Persistencia con Spring Data JPA. Contiene subpaquete `entity/` (entidades JPA) y `adapter/` (adaptadores que conectan el repositorio con el servicio).
4. **`model`**: Modelos de dominio inmutables ricos y subpaquete **`vo/`** con Value Objects.
5. **`enums`**: Enums propios del dominio del módulo.
6. **`exception`**: Fábricas de excepciones de dominio que extienden de `DomainException`.

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

## 📚 Documentos de Reglas Específicas
Para consultar directrices detalladas, revisa:
- [Arquitectura y Value Objects](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/.agents/rules/architecture.md)
- [Flujo de Trabajo y Planificación](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/.agents/rules/workflow.md)
- [Estándares Técnicos (Java 25 & Spring Boot)](file:///d:/Documentos/VS/frameworks/Spring%20boot/Todo/.agents/rules/tech-stack.md)
