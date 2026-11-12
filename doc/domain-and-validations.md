# Dominio, Value Objects y Reglas de Validación

El núcleo de cada módulo reside en su paquete `model/`, compuesto por un **Modelo de Dominio Inmutable** (`*Model.java`), **Value Objects** (`model/vo/*`) implementados con `record` de Java 25, **Enums** (`model/enums/*`) y **Excepciones de Dominio** (`exception/*`).

---

## 1. Constantes Centralizadas (`MyConst`)

Definidas en [`MyConst.java`](../src/main/java/com/rafael/todo/shared/MyConst.java) dentro del paquete `shared`:

| Constante | Valor | Uso |
| :--- | :--- | :--- |
| `MAX_EMAIL_LENGTH` | `"255"` | Longitud máxima del correo de usuario |
| `EMAIL_REGEX` | `"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"` | Expresión regular de validación de email |
| `MIN_NAME_LENGTH` | `"2"` | Longitud mínima del nombre de usuario |
| `MAX_NAME_LENGTH` | `"100"` | Longitud máxima del nombre de usuario |
| `MIN_PASSWORD_LENGTH` | `"8"` | Longitud mínima de la contraseña en texto plano |
| `MIN_CATEGORY_NAME_LENGTH` | `"2"` | Longitud mínima del nombre de categoría |
| `MAX_CATEGORY_NAME_LENGTH` | `"50"` | Longitud máxima del nombre de categoría |
| `HEX_COLOR_REGEX` | `"^#[A-Fa-f0-9]{6}$"` | Formato de color hexadecimal (`#RRGGBB`) |
| `MIN_TASK_TITLE_LENGTH` | `"2"` | Longitud mínima del título de tarea |
| `MAX_TASK_TITLE_LENGTH` | `"150"` | Longitud máxima del título de tarea |
| `MAX_TASK_DESCRIPTION_LENGTH` | `"2000"` | Longitud máxima de la descripción de tarea |
| `MIN_SUBTASK_TITLE_LENGTH` | `"2"` | Longitud mínima del título de subtarea |
| `MAX_SUBTASK_TITLE_LENGTH` | `"200"` | Longitud máxima del título de subtarea |
| `MAX_TOKEN_HASH_LENGTH` | `"255"` | Longitud máxima del hash/valor del refresh token |
| `COOKIE_NAME` | `"token"` | Nombre de la cookie `HttpOnly` para el refresh token |

---

## 2. Patrón de Modelos de Dominio Inmutables (*Copy-on-Write*)

Todos los modelos (`RefreshTokenModel`, `UserModel`, `CategoryModel`, `TaskModel`, `SubtaskModel`) cumplen las siguientes reglas:
1. **Inmutabilidad total**: Clase declarada `public final class` con todos sus atributos `private final`.
2. **Constructor privado**: `@AllArgsConstructor(access = AccessLevel.PRIVATE)` para obligar el uso de fábricas estáticas.
3. **Fábricas estáticas**:
   - `create(...)`: Instancia un nuevo objeto de dominio aplicando valores por defecto (ej. `RefreshTokenRevoked.active()`, `UserId.random()`, `UserRole.user()`, `TaskStatus.pending()`, `SubtaskCompleted.pending()`, timestamps `LocalDateTime.now()`).
   - `reconstitute(...)`: Reconstruye el objeto desde la base de datos preservando su `id` y timestamps históricos sin alterarlos.
4. **Mutaciones por copia (`copy(...)`)**: Métodos expresivos de negocio que devuelven una nueva instancia inmutable:
   - `RefreshTokenModel`: `revoke()`, `isExpired()`, `validateActive()`.
   - `UserModel`: `changeName(...)`, `changePassword(...)`, `changeRole(...)`, `activate()`, `deactivate()`.
   - `CategoryModel`: `changeName(...)`, `changeColor(...)`.
   - `TaskModel`: `changeTitle(...)`, `changeDescription(...)`, `changeCategory(...)`, `changePriority(...)`, `changeDueDate(...)`, `changeStatus(...)`, `markCompleted()`, `markPending()`.
   - `SubtaskModel`: `changeTitle(...)`, `changePosition(...)`, `markCompleted()`, `markPending()`, `toggleCompleted()`.

---

## 3. Catálogo de Value Objects y Validaciones por Módulo

### 3.1. Módulo `auth`

| Value Object | Tipo Interno | Reglas / Invariantes en Constructor Compacto | Excepción Asociada |
| :--- | :--- | :--- | :--- |
| `RefreshTokenId` | `Long` | No nulo y $> 0$. Soporta parseo desde `String`. | `RefreshTokenIdException` (400 / 404) |
| `RefreshTokenUserId` | `UUID` | No nulo. Soporta parseo desde `String` con formato UUID válido. | `RefreshTokenUserIdException` (400) |
| `RefreshTokenHash` | `String` | No nulo/vacío, se aplica `trim()`, longitud $\le 255$ (`MAX_TOKEN_HASH_LENGTH`). | `RefreshTokenException` (400 / 401 / 404) |
| `RefreshTokenExpiration` | `LocalDateTime` | No nulo. Soporta parseo desde ISO `String` y método `isExpired()`. | `RefreshTokenExpirationException` (400) |
| `RefreshTokenRevoked` | `boolean` | Estado de revocación (`active()`, `revoked()`). `revoke()` falla si ya está revocado. | `RefreshTokenException` (400 / 401) |

### 3.2. Módulo `user`

| Value Object | Tipo Interno | Reglas / Invariantes en Constructor Compacto | Excepción Asociada |
| :--- | :--- | :--- | :--- |
| `UserId` | `UUID` | No puede ser `null`. Incluye fábrica `UserId.random()`. | `UserUuidException` (400 / 404) |
| `UserName` | `String` | No nulo/vacío, se aplica `trim()`, longitud entre `2` y `100` caracteres. | `UserNameException` (400) |
| `UserEmail` | `String` | No nulo/vacío, `trim()`, longitud $\le 255$, debe cumplir `EMAIL_REGEX`. | `UserEmailException` (400 / 409) |
| `UserPassword` | `String` | No nulo/vacío, longitud mínima de `8` caracteres en `fromRaw(...)`. | `UserPasswordException` (400) |
| `UserRole` | `UserRoleEnum` | No nulo. Soporta creación desde `String` (`ROLE_USER`, `ROLE_ADMIN`). | `UserRoleException` (400) |
| `UserAccount` | `boolean` | Estado activo/inactivo de la cuenta (`active()`, `inactive()`). | `UserAccountException` |

### 3.3. Módulo `category`

| Value Object | Tipo Interno | Reglas / Invariantes en Constructor Compacto | Excepción Asociada |
| :--- | :--- | :--- | :--- |
| `CategoryId` | `Long` | No nulo y $> 0$. | `CategoryIdException` (400 / 404) |
| `CategoryUserId` | `UUID` | No nulo. Representa el propietario de la categoría. | `CategoryUserIdException` (400) |
| `CategoryName` | `String` | No nulo/vacío, `trim()`, longitud entre `2` y `50` caracteres. | `CategoryNameException` (400 / 409) |
| `CategoryColor` | `String` | Si es nulo o vacío asigna `#3B82F6`; debe cumplir `HEX_COLOR_REGEX`. | `CategoryColorException` (400) |

### 3.4. Módulo `task`

| Value Object | Tipo Interno | Reglas / Invariantes en Constructor Compacto | Excepción Asociada |
| :--- | :--- | :--- | :--- |
| `TaskId` | `Long` | No nulo y $> 0$. | `TaskIdException` (400 / 404) |
| `TaskUserId` | `UUID` | No nulo. Usuario propietario de la tarea. | `TaskUserIdException` (400) |
| `TaskCategoryId` | `Long` | Opcional (`null` permitido); si está presente debe ser $> 0$. | `TaskCategoryIdException` (400) |
| `TaskTitle` | `String` | No nulo/vacío, `trim()`, longitud entre `2` y `150` caracteres. | `TaskTitleException` (400) |
| `TaskDescription` | `String` | Opcional (`null` permitido); si está presente longitud $\le 2000$ caracteres. | `TaskDescriptionException` (400) |
| `TaskStatus` | `TaskStatusEnum` | No nulo. Valores: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`. | `TaskStatusException` (400) |
| `TaskPriority` | `TaskPriorityEnum` | No nulo. Valores: `LOW`, `MEDIUM`, `HIGH`, `URGENT` (por defecto `MEDIUM`). | `TaskPriorityException` (400) |
| `TaskDueDate` | `LocalDateTime` | Opcional (`null` permitido). | — |

### 3.5. Módulo `subtask`

| Value Object | Tipo Interno | Reglas / Invariantes en Constructor Compacto | Excepción Asociada |
| :--- | :--- | :--- | :--- |
| `SubtaskId` | `Long` | No nulo y $> 0$. | `SubtaskIdException` (400 / 404) |
| `SubtaskTaskId` | `Long` | No nulo y $> 0$. Identificador de la tarea padre. | `SubtaskTaskIdException` (400) |
| `SubtaskTitle` | `String` | No nulo/vacío, `trim()`, longitud entre `2` y `200` caracteres. | `SubtaskTitleException` (400) |
| `SubtaskCompleted` | `boolean` | Estado booleano (`completed()`, `pending()`). | — |
| `SubtaskPosition` | `Integer` | Si es `null` asigna `0`; no puede ser negativo ($< 0$). | `SubtaskPositionException` (400) |
