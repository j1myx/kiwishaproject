@echo off
setlocal

REM Arranca el servidor Spring Boot usando el Maven Wrapper del proyecto.
REM Ejecuta este archivo con doble clic o desde CMD/PowerShell.

cd /d "%~dp0"
echo.
echo === Achisway / Kiwisha - Iniciando servidor en http://localhost:8080 ===
echo Directorio: %CD%
echo.

call "%CD%\mvnw.cmd" spring-boot:run

echo.
echo El servidor se ha detenido. Presiona una tecla para cerrar...
pause >nul
endlocal
