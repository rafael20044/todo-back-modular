# Referencia de la API REST

Todos los endpoints están publicados bajo el prefijo global **`/api`** configurado en `server.servlet.context-path`.

- **Base URL Local**: `http://localhost:8080/api`
- **Formato de Intercambio**: `application/json`
- **Paginación**: Todos los listados (`GET`) devuelven un objeto `Page<T>` estándar de Spring Data.
- **Eliminación (`DELETE`)**: Siempre retorna `204 No Content` sin cuerpo.

---

## 1. Módulo de Usuarios (`/api/users`)

Controlador: [`UserController.java`](../src/main/java/com/rafael/todo/module/user/controller/UserController.java)

| Método | Endpoint | Descripción | Status Éxito |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/users` | Crea un nuevo usuario | `201 Created` |
| `GET` | `/api/users` | Lista usuarios paginados (`?page=0&size=10`) | `200 OK` |
| `GET` | `/api/users/{uuid}` | Obtiene un usuario por su `UUID` | `200 OK` |
| `PUT` | `/api/users/{uuid}` | Actualiza nombre, contraseña o rol de un usuario | `200 OK` |
| `DELETE` | `/api/users/{uuid}` | Elimina un usuario por su `UUID` | `204 No Content` |

### `POST /api/users` — Crear Usuario
**Request Body (`CreateUserDto`):**
```json
{
  "username": "Rafael Perez",
  "email": "rafael@example.com",
  "password": "Password123!",
  "role": "ROLE_USER"
}
```
> Nota: Si `role` es `null` o se omite, se asigna `"ROLE_USER"` por defecto. Valores permitidos: `ROLE_USER`, `ROLE_ADMIN`.

**Response (`201 Created` - `ResponseUserDto`):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "Rafael Perez",
  "email": "rafael@example.com",
  "role": "ROLE_USER",
  "createdAt": "2026-11-04T17:00:00",
  "updatedAt": "2026-11-04T17:00:00"
}
```

### `PUT /api/users/{uuid}` — Actualizar Usuario
**Request Body (`UpdateUserDto`):** (todos los campos son opcionales; solo se actualizan los enviados)
```json
{
  "username": "Rafael Actualizado",
  "password": "NewPassword456!",
  "role": "ROLE_ADMIN"
}
```

---

## 2. Módulo de Categorías (`/api/categories`)

Controlador: [`CategoryController.java`](../src/main/java/com/rafael/todo/module/category/controller/CategoryController.java)

| Método | Endpoint | Descripción | Status Éxito |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/categories` | Crea una categoría para un usuario | `201 Created` |
| `GET` | `/api/categories` | Lista categorías (`?userId=...&page=0&size=20`) | `200 OK` |
| `GET` | `/api/categories/{id}` | Obtiene una categoría por su `id` (`Long`) | `200 OK` |
| `PUT` | `/api/categories/{id}` | Actualiza el nombre o color de una categoría | `200 OK` |
| `DELETE` | `/api/categories/{id}` | Elimina una categoría por su `id` | `204 No Content` |

### `POST /api/categories` — Crear Categoría
**Request Body (`CreateCategoryDto`):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Trabajo",
  "color": "#3B82F6"
}
```
> Nota: Si `color` se envía vacío o `null`, se asigna `#3B82F6` por defecto. Valida previamente que `userId` exista (404 si no existe) y que el usuario no tenga otra categoría con el mismo nombre (409 si se repite).

**Response (`201 Created` - `ResponseCategoryDto`):**
```json
{
  "id": 1,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Trabajo",
  "color": "#3B82F6",
  "createdAt": "2026-11-04T17:05:00"
}
```

### `PUT /api/categories/{id}` — Actualizar Categoría
**Request Body (`UpdateCategoryDto`):**
```json
{
  "name": "Trabajo Urgente",
  "color": "#EF4444"
}
```

---

## 3. Módulo de Tareas (`/api/tasks`)

Controlador: [`TaskController.java`](../src/main/java/com/rafael/todo/module/task/controller/TaskController.java)

| Método | Endpoint | Descripción | Status Éxito |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/tasks` | Crea una nueva tarea (valida `userId` y `categoryId`) | `201 Created` |
| `GET` | `/api/tasks` | Lista tareas con filtros opcionales y paginación | `200 OK` |
| `GET` | `/api/tasks/{id}` | Obtiene una tarea por su `id` (`Long`) | `200 OK` |
| `PUT` | `/api/tasks/{id}` | Actualiza atributos o estado de una tarea | `200 OK` |
| `DELETE` | `/api/tasks/{id}` | Elimina una tarea por su `id` | `204 No Content` |

### Filtros Query Params en `GET /api/tasks`
- `userId` (`UUID`, opcional): Filtra tareas de un usuario específico.
- `categoryId` (`Long`, opcional): Filtra tareas de una categoría.
- `status` (`TaskStatusEnum`, opcional): `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
- `page` (`int`, por defecto `0`): Número de página.
- `size` (`int`, por defecto `20`): Tamaño de página.

### `POST /api/tasks` — Crear Tarea
**Request Body (`CreateTaskDto`):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "categoryId": 1,
  "title": "Preparar presentación trimestral",
  "description": "Incluir métricas de rendimiento del Q4",
  "priority": "HIGH",
  "dueDate": "2026-11-15T18:00:00"
}
```
> Nota: Al crearse, toda tarea inicia con `status = "PENDING"` y `completedAt = null`. Si `priority` es `null`, toma `"MEDIUM"`. `categoryId`, `description` y `dueDate` son opcionales.

**Response (`201 Created` - `ResponseTaskDto`):**
```json
{
  "id": 10,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "categoryId": 1,
  "title": "Preparar presentación trimestral",
  "description": "Incluir métricas de rendimiento del Q4",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2026-11-15T18:00:00",
  "completedAt": null,
  "createdAt": "2026-11-04T17:10:00",
  "updatedAt": "2026-11-04T17:10:00"
}
```

### `PUT /api/tasks/{id}` — Actualizar Tarea
**Request Body (`UpdateTaskDto`):**
```json
{
  "categoryId": 1,
  "title": "Preparar presentación trimestral final",
  "description": "Incluir métricas de rendimiento y proyecciones",
  "status": "COMPLETED",
  "priority": "URGENT",
  "dueDate": "2026-11-15T18:00:00"
}
```
> Nota: Cuando `status` cambia a `"COMPLETED"`, el modelo asigna automáticamente la fecha y hora actual en `completedAt`. Si cambia a otro estado distinto de `"COMPLETED"`, `completedAt` vuelve a `null`.

---

## 4. Módulo de Subtareas (`/api/subtasks`)

Controlador: [`SubtaskController.java`](../src/main/java/com/rafael/todo/module/subtask/controller/SubtaskController.java)

| Método | Endpoint | Descripción | Status Éxito |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/subtasks` | Crea una subtarea asociada a una tarea existente | `201 Created` |
| `GET` | `/api/subtasks` | Lista subtareas de una tarea (`?taskId=...&page=0&size=20`) | `200 OK` |
| `GET` | `/api/subtasks/{id}` | Obtiene una subtarea por su `id` (`Long`) | `200 OK` |
| `PUT` | `/api/subtasks/{id}` | Actualiza título, estado `isCompleted` o posición | `200 OK` |
| `DELETE` | `/api/subtasks/{id}` | Elimina una subtarea por su `id` | `204 No Content` |

### `POST /api/subtasks` — Crear Subtarea
**Request Body (`CreateSubtaskDto`):**
```json
{
  "taskId": 10,
  "title": "Exportar gráficos de ventas",
  "position": 1
}
```
> Nota: Toda subtarea se crea con `isCompleted = false`. Si `position` se omite o es `null`, se asigna `0` por defecto. Valida previamente que la tarea `taskId` exista.

**Response (`201 Created` - `ResponseSubtaskDto`):**
```json
{
  "id": 100,
  "taskId": 10,
  "title": "Exportar gráficos de ventas",
  "isCompleted": false,
  "position": 1,
  "createdAt": "2026-11-04T17:12:00",
  "updatedAt": "2026-11-04T17:12:00"
}
```

### `PUT /api/subtasks/{id}` — Actualizar Subtarea
**Request Body (`UpdateSubtaskDto`):**
```json
{
  "title": "Exportar gráficos de ventas validados",
  "isCompleted": true,
  "position": 2
}
```

---

## 5. Estructura de Respuestas Paginadas y Errores

### Estructura de Página (`Page<Response*Dto>`)
```json
{
  "content": [
    { "...": "..." }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

### Estructura Estándar de Error (`ErrorDto`)
Cualquier error de negocio (`400`, `404`, `409`) o de servidor (`500`) retorna la estructura definida en [`ErrorDto.java`](../src/main/java/com/rafael/todo/shared/ErrorDto.java):

```json
{
  "code": "task.id.not.found",
  "message": "Task with ID 999 not found",
  "status": 404
}
```

