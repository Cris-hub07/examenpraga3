@echo off
setlocal
cd /d "%~dp0"

if not exist out (
    mkdir out
)

javac -encoding UTF-8 -d out src\entidades\*.java src\accesodatos\*.java src\logicanegocio\*.java src\presentacion\*.java

if errorlevel 1 (
    echo.
    echo No se pudo compilar la aplicacion.
    echo Revisa los errores mostrados arriba.
    pause
    exit /b 1
)

start "" javaw -cp out presentacion.Main
exit /b 0
