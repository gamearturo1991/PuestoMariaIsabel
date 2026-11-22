@echo off
rem ============================================================
rem  Respaldo de la base de datos PuestoMI (SOLO LECTURA)
rem  Genera un archivo .sql con fecha y hora. No modifica la base.
rem  Uso: doble clic, o el Programador de tareas de Windows.
rem ============================================================
setlocal EnableExtensions
set "AQUI=%~dp0"

if not exist "%AQUI%respaldo.config" (
    echo No existe respaldo.config. Copia respaldo.config.ejemplo como respaldo.config y ajustalo.
    if "%~1"=="" pause
    exit /b 1
)

rem Valores por defecto y lectura del archivo de configuracion
set "MYSQL_BIN="
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=PuestoMI"
set "DB_USER=root"
set "DB_PASS="
set "DIAS_CONSERVAR=30"
set "CARPETA_COPIAS="
for /f "usebackq eol=# tokens=1* delims==" %%A in ("%AQUI%respaldo.config") do set "%%A=%%B"

if "%CARPETA_COPIAS%"=="" set "CARPETA_COPIAS=%AQUI%copias"
if not exist "%CARPETA_COPIAS%" mkdir "%CARPETA_COPIAS%"

set "DUMP=mysqldump"
if not "%MYSQL_BIN%"=="" set "DUMP=%MYSQL_BIN%\mysqldump.exe"

for /f %%I in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HH-mm"') do set "FECHA=%%I"
set "ARCHIVO=%CARPETA_COPIAS%\PuestoMI_%FECHA%.sql"
set "LOG=%CARPETA_COPIAS%\respaldo.log"

rem La contrasena se pasa por variable de entorno para que no aparezca en la linea de comandos.
set "MYSQL_PWD=%DB_PASS%"

"%DUMP%" --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --single-transaction --routines --triggers --default-character-set=utf8mb4 --result-file="%ARCHIVO%.tmp" %DB_NAME%
if errorlevel 1 (
    if exist "%ARCHIVO%.tmp" del "%ARCHIVO%.tmp"
    echo %date% %time% ERROR: no se pudo generar el respaldo.>>"%LOG%"
    echo ERROR: no se pudo generar el respaldo. Revisa respaldo.config y que MySQL este encendido.
    if "%~1"=="" pause
    exit /b 1
)

rem Solo se conserva el archivo si el respaldo termino bien
move /y "%ARCHIVO%.tmp" "%ARCHIVO%" >nul
echo %date% %time% OK: %ARCHIVO%>>"%LOG%"
echo Respaldo listo: %ARCHIVO%

rem Borra los respaldos automaticos con mas de DIAS_CONSERVAR dias (solo archivos PuestoMI_*.sql de esa carpeta)
forfiles /p "%CARPETA_COPIAS%" /m "PuestoMI_*.sql" /d -%DIAS_CONSERVAR% /c "cmd /c del @path" >nul 2>&1

if "%~1"=="" pause
exit /b 0
