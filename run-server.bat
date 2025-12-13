@echo off
setlocal

REM Arranca el servidor Spring Boot usando el Maven Wrapper del proyecto.
REM Ejecuta este archivo con doble clic o desde CMD/PowerShell.

cd /d "%~dp0"
echo.
echo === Achisway / Kiwisha - Iniciando servidor en http://localhost:8080 ===
echo Directorio: %CD%
echo.

REM ============================================
REM Mercado Pago (opcional en desarrollo)
REM ============================================
REM Si vas a probar pagos, define estas variables.
REM Opción recomendada: crea un archivo local .env.bat (NO se sube a git)
REM con el contenido:
REM   set "APP_PUBLIC_BASE_URL=https://..."  (tu dominio público o túnel HTTPS)
REM   set "MERCADOPAGO_ACCESS_TOKEN=..."
REM   set "MERCADOPAGO_PUBLIC_KEY=..."
REM y este script lo cargará automáticamente.
if exist ".env.bat" (
	echo Cargando variables desde .env.bat
	call ".env.bat"
)
REM ============================================

call "%CD%\mvnw.cmd" spring-boot:run

echo.
echo El servidor se ha detenido. Presiona una tecla para cerrar...
pause >nul
endlocal
