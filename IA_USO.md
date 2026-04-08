# Uso De Inteligencia Artificial En El Desarrollo

## Proposito

Este documento registra el uso de inteligencia artificial como apoyo en el desarrollo del proyecto **Sistema de Control de Acceso a Laboratorio**.
La IA se utilizo para acelerar la creacion de estructura, clases base, persistencia, logica de negocio, interfaz de consola y documentacion.

## Lista De Prompts Utilizados

### 1. Estructura base del proyecto

**Prompt utilizado:**

`Crea la estructura base de un proyecto Java con arquitectura en capas para un sistema llamado "Sistema de Control de Acceso a Laboratorio".`

**Parte generada:**

- Paquetes `entidades`, `accesodatos`, `logicanegocio` y `presentacion`
- Clases base `Usuario`, `Acceso`, `UsuarioDAO`, `AccesoDAO`, `UsuarioService`, `AccesoService` y `Main`

### 2. Entidades del sistema

**Prompt utilizado:**

`Implementa las clases de entidad para un sistema de control de acceso a laboratorio.`

**Parte generada:**

- Clase `Usuario`
- Clase `Acceso`
- Atributos, encapsulamiento, constructores y `toString()`

### 3. Capa de acceso a datos

**Prompt utilizado:**

`Implementa la capa de acceso a datos usando archivos .txt en Java.`

**Parte generada:**

- Clase `UsuarioDAO`
- Clase `AccesoDAO`
- Persistencia en archivos `.txt`
- Metodos `guardar`, `listar` y `eliminar` segun correspondia

### 4. Logica de negocio

**Prompt utilizado:**

`Implementa la logica de negocio para el sistema de control de acceso.`

**Parte generada:**

- Clase `UsuarioService`
- Clase `AccesoService`
- Reglas para registro de usuarios, entradas y salidas
- Integracion entre servicios y DAO

### 5. Interfaz de consola

**Prompt utilizado:**

`Crea una interfaz de consola en Java para el sistema de control de acceso a laboratorio.`

**Parte generada:**

- Clase `Main`
- Menu de opciones
- Interaccion por consola con `Scanner`
- Manejo basico de errores con mensajes al usuario

### 6. Funcionalidades de reporte

**Prompt utilizado:**

`Agrega funcionalidades de reporte en AccesoService:`

**Parte generada:**

- Historial de accesos por usuario
- Calculo de tiempo total en laboratorio usando `LocalDateTime` y `Duration`

### 7. Documentacion del proyecto

**Prompts utilizados:**

- `Genera un README.md para un proyecto Java llamado "Sistema de Control de Acceso a Laboratorio".`
- `Genera un CHANGELOG.md con las versiones:`
- `Genera un archivo IA_USO.md que documente el uso de inteligencia artificial en el desarrollo del sistema.`

**Parte generada:**

- `README.md`
- `CHANGELOG.md`
- `IA_USO.md`

## Ajustes Manuales Realizados

Durante la integracion del codigo generado se realizaron ajustes tecnicos para mantener coherencia y separacion de capas:

- Se agrego en `AccesoDAO` el metodo `guardarTodos(...)` para actualizar correctamente una salida sin acceder a archivos desde la presentacion.
- Se ajusto la persistencia de `fechaHoraSalida` para permitir valores vacios cuando un acceso aun no ha sido cerrado.
- Se corrigio el calculo de tiempo total en `AccesoService` para no contar accesos sin salida registrada.
- Se mantuvo la regla de que `Main` solo consume servicios y no accede directamente a DAO.
- Se verifico la compilacion del sistema con `javac` y la ejecucion del menu por consola.

## Justificacion Tecnica Del Uso De IA

La inteligencia artificial se utilizo como herramienta de apoyo y no como sustitucion del desarrollo tecnico.

Su uso fue adecuado porque:

- Acelero la generacion de codigo repetitivo y estructural, como clases base, getters, setters y menus.
- Facilito la organizacion inicial del proyecto bajo arquitectura en capas.
- Permitio producir rapidamente documentacion tecnica consistente con el estado del sistema.

No sustituyo el criterio tecnico porque:

- Las decisiones de arquitectura, separacion de responsabilidades y reglas del sistema requirieron revision e integracion consciente.
- Fue necesario ajustar codigo generado para resolver detalles de persistencia y coherencia entre capas.
- La validacion final dependio de compilacion, prueba de ejecucion y revision del comportamiento esperado.

En conclusion, la IA funciono como asistente de productividad para acelerar el desarrollo, mientras que la definicion de reglas, correcciones, integracion y verificacion tecnica siguieron dependiendo de supervision y criterio humano.
