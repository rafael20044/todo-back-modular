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
   - **Regla de Esqueletos para Implementaciones**: En clases que implementan interfaces o contratos —específicamente **servicios (`*Service`)**, **adaptadores (`*Adapter`)** y **controladores (`*Controller`)**— el asistente debe generar exclusivamente el **esqueleto técnico** (anotaciones, inyección de dependencias por constructor con `@Autowired`, firmas de métodos y cuerpos retornando `null;` o vacíos), sin lógica interna a menos que el usuario lo solicite explícitamente.
   - **Mappers Completos**: Los mappers (`*MapperController`, `*MapperService`, `*MapperRepository`) sí se implementan completos al ser transformaciones puras de datos.
   - **Sincronización de Documentación y Tests**: Si se agrega o modifica un módulo, mantener actualizados los documentos en `/doc` y las suites de pruebas unitarias en `src/test/java`.
   - No realizar cambios no especificados en el plan sin consultar previamente.

3. **Fase de Verificación y Validación**:
   - **Prohibido ejecutar `.\gradlew.bat compileJava`, `.\gradlew.bat test` o cualquier tarea de Gradle en terminal**, ya que causan bloqueos en el entorno o violaciones de sandbox.
   - La verificación se realiza de forma estricta mediante **análisis estático de código** (verificación de firmas, tipos, importaciones y cumplimiento de interfaces).
   - Informar al usuario de los archivos generados y el estado final.
