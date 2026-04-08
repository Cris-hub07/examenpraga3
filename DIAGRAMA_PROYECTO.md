# Diagrama del Proyecto

Este documento resume la estructura actual del sistema y como se comunican sus capas principales.

## Arquitectura General

```mermaid
flowchart TD
    Operador[Usuario de consola]
    Main[presentacion.Main]

    UsuarioService[logicanegocio.UsuarioService]
    AccesoService[logicanegocio.AccesoService]

    UsuarioDAO[accesodatos.UsuarioDAO]
    AccesoDAO[accesodatos.AccesoDAO]

    UsuarioEntidad[entidades.Usuario]
    AccesoEntidad[entidades.Acceso]

    UsuariosTxt[(data/usuarios.txt)]
    AccesosTxt[(data/accesos.txt)]

    Operador --> Main

    Main --> UsuarioService
    Main --> AccesoService

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
    participant US as UsuarioService
    participant AS as AccesoService
    participant UDAO as UsuarioDAO
    participant ADAO as AccesoDAO
    participant Data as Archivos TXT

    Operador->>Main: Selecciona una opcion del menu

    alt Registro y gestion de usuarios
        Main->>US: guardarUsuario(), listarUsuarios(), eliminarUsuario()
        US->>UDAO: guardar(), listar(), eliminar()
        UDAO->>Data: Lee/escribe usuarios.txt
    else Control de accesos
        Main->>AS: registrarEntrada(), registrarSalida(), listarAccesos(), calcularTiempoTotalEnLaboratorio()
        AS->>UDAO: listar()
        AS->>ADAO: guardar(), listar(), guardarTodos()
        UDAO->>Data: Lee usuarios.txt
        ADAO->>Data: Lee/escribe accesos.txt
    end
```

## Responsabilidad por Capa

- `presentacion`: muestra el menu, captura datos del usuario y presenta resultados.
- `logicanegocio`: valida reglas del sistema, evita duplicados y controla accesos activos.
- `accesodatos`: persiste la informacion en archivos de texto dentro de `data/`.
- `entidades`: define los objetos de dominio `Usuario` y `Acceso`.

## Relacion Entre Clases

```text
Main
|- usa -> UsuarioService
|  |- usa -> UsuarioDAO
|  |- crea/retorna -> Usuario
|
|- usa -> AccesoService
   |- usa -> UsuarioDAO
   |- usa -> AccesoDAO
   |- crea/retorna -> Acceso

UsuarioDAO <-> data/usuarios.txt
AccesoDAO  <-> data/accesos.txt
```
