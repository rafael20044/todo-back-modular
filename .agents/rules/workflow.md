# Flujo de Trabajo y Protocolo de Planificación Obligatoria

Este documento establece las reglas obligatorias de interacción y desarrollo para el agente en este proyecto.

---

## 🛑 REGLA MAESTRA: "Plan Primero, Ejecución Después"

El asistente **NUNCA** debe comenzar a crear archivos, modificar código existente o aplicar cambios estructurales sin haber presentado previamente un plan detallado y obtenido la confirmación o retroalimentación del usuario.

### Estructura Obligatoria de Cada Plan

Antes de escribir código, el asistente debe emitir un mensaje estructurado con las siguientes secciones:

```markdown
### 📋 Plan de Acción: [Nombre de la Tarea/Característica]

#### 1. Objetivo y Alcance
- Explicación breve de qué necesidad o requisito se va a abordar.

#### 2. Componentes y Archivos Afectados
- Lista exhaustiva de archivos a crear, modificar o eliminar con sus rutas exactas.
- Capas y módulos involucrados.

#### 3. Cómo se va a Implementar (Estrategia Técnica)
- Diseño de clases, interfaces, Value Objects o entidades.
- Patrones de diseño aplicados.
- Contratos de entrada y salida (DTOs / APIs).
- Migración Flyway requerida (si aplica).

#### 4. Sugerencias de Mejora / Alternativas
- Opciones arquitectónicas evaluadas.
- Riesgos potenciales y mitigaciones sugeridas.

#### 5. Próximo Paso
- Preguntar al usuario si está de acuerdo con el plan para proceder a la implementación.
```

---

## Ciclo de Vida de una Tarea

1. **Fase de Propuesta y Planificación**:
   - Analizar el requerimiento del usuario.
   - Presentar el plan estructurado y sugerencias de mejora.
   - Esperar la respuesta/confirmación del usuario.

2. **Fase de Implementación**:
   - Una vez aprobado el plan, crear y modificar los archivos correspondientes siguiendo los estándares de arquitectura modular y capas.
   - No realizar cambios no especificados en el plan sin consultar previamente.

3. **Fase de Verificación y Validación**:
   - Compilar el proyecto con `./gradlew compileJava` o ejecutar tests con `./gradlew test` para asegurar que el código compila sin errores.
   - Informar al usuario de los archivos generados y el estado final.
