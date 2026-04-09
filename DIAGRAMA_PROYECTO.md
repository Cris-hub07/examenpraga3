# Diagrama del Proyecto

Este documento resume la estructura actual del sistema y como se comunican sus capas principales.

## Arquitectura General

```mermaid
flowchart TD
    Operador[Usuario del sistema]
    Main[presentacion.Main]
    Ventana[presentacion.VentanaPrincipal]
    Consola[presentacion.ConsolaApp]

    UsuarioService[logicanegocio.UsuarioService]
    AccesoService[logicanegocio.AccesoService]

    UsuarioDAO[accesodatos.UsuarioDAO]
    AccesoDAO[accesodatos.AccesoDAO]

    UsuarioEntidad[entidades.Usuario]
    AccesoEntidad[entidades.Acceso]

    UsuariosTxt[(data/usuarios.txt)]
    AccesosTxt[(data/accesos.txt)]

    Operador --> Main
    Main --> Ventana
    Main -. opcion alterna .-> Consola

    Ventana --> UsuarioService
    Ventana --> AccesoService
    Consola --> UsuarioService
    Consola --> AccesoService

    UsuarioService --> UsuarioEntidad
    UsuarioService --> UsuarioDAO

    AccesoService --> AccesoEntidad
    AccesoService --> UsuarioDAO
    AccesoService --> AccesoDAO

    UsuarioDAO --> UsuarioEntidad
    UsuarioDAO --> UsuariosTxt

    AccesoDAO --> AccesoEntidad
    AccesoDAO --> AccesosTxt
```

## Flujo de Operacion

```mermaid
sequenceDiagram
    actor Operador
    participant Main as Main
    participant UI as VentanaPrincipal
    participant CLI as ConsolaApp
    participant US as UsuarioService
    participant AS as AccesoService
    participant UDAO as UsuarioDAO
    participant ADAO as AccesoDAO
    participant Data as Archivos TXT

    Operador->>Main: Abre la aplicacion
    Main->>UI: Inicia interfaz grafica
    Main-->>CLI: Alternativa de consola
    Operador->>UI: Registra usuarios o movimientos

    alt Registro y gestion de usuarios
        UI->>US: guardarUsuario(), actualizarUsuario(), listarUsuarios(), eliminarUsuario()
        US->>UDAO: guardar(), actualizar(), listar(), eliminar(), buscarPorId()
        UDAO->>Data: Lee/escribe usuarios.txt
    else Control de accesos
        UI->>AS: registrarEntrada(), registrarSalida(), listarAccesos(), calcularTiempoTotalEnLaboratorio()
        AS->>UDAO: listar()
        AS->>ADAO: guardar(), listar(), guardarTodos()
        UDAO->>Data: Lee usuarios.txt
        ADAO->>Data: Lee/escribe accesos.txt
    end
```

## Responsabilidad por Capa

- `presentacion`: abre la interfaz Swing, mantiene una alternativa de consola y presenta resultados al usuario.
- `logicanegocio`: valida reglas del sistema, evita duplicados y controla accesos activos.
- `accesodatos`: persiste la informacion en archivos de texto dentro de `data/`.
- `entidades`: define los objetos de dominio `Usuario` y `Acceso`.

## Relacion Entre Clases

```text
Main
|- inicia -> VentanaPrincipal
|- opcion alterna -> ConsolaApp
|
VentanaPrincipal
|- usa -> UsuarioService
|  |- usa -> UsuarioDAO
|  |- crea/retorna -> Usuario
|
|- usa -> AccesoService
   |- usa -> UsuarioDAO
   |- usa -> AccesoDAO
   |- crea/retorna -> Acceso

ConsolaApp
|- usa -> UsuarioService
|- usa -> AccesoService

UsuarioDAO <-> data/usuarios.txt
AccesoDAO  <-> data/accesos.txt
```
