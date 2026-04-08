# Sistema de Control de Acceso a Laboratorio

## Descripcion

Este proyecto es una aplicacion de consola en Java para gestionar el acceso de usuarios a un laboratorio.
Permite registrar usuarios, controlar entradas y salidas, consultar el historial de accesos y calcular el tiempo total de permanencia dentro del laboratorio.

El sistema esta organizado con arquitectura en capas para mantener una separacion clara entre entidades, acceso a datos, logica de negocio y presentacion.

## Como ejecutar

1. Compilar el proyecto:

```bash
javac -d out src/entidades/*.java src/accesodatos/*.java src/logicanegocio/*.java src/presentacion/*.java
```

2. Ejecutar la aplicacion:

```bash
java -cp out presentacion.Main
```

Nota:
Los datos se almacenan en archivos `.txt` dentro de la carpeta `data`.

## Estructura Del Proyecto Por Capas

- `src/entidades`
Contiene las clases del modelo del sistema, como `Usuario` y `Acceso`.

- `src/accesodatos`
Contiene los DAO encargados de leer y escribir la informacion en archivos `.txt`.

- `src/logicanegocio`
Contiene los servicios que aplican las reglas del sistema, como registro de usuarios, entradas, salidas y reportes.

- `src/presentacion`
Contiene la interfaz de consola y el punto de entrada del programa en la clase `Main`.

## Tecnologias Utilizadas

- Java
- Programacion orientada a objetos
- Arquitectura en capas
- Manejo de archivos `.txt`
- `BufferedReader` y `BufferedWriter`
- `LocalDateTime` y `Duration`
