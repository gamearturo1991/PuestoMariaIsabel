@echo off
rem ============================================================
rem  Restaura un respaldo de PuestoMI  (ESTO SI MODIFICA LA BASE)
rem  Reemplaza los datos actuales por los del respaldo elegido.
rem  Antes guarda una copia del estado actual para poder deshacer.
rem  Uso: restaurar.bat   (o arrastra un archivo .sql sobre el)
rem ============================================================
setlocal EnableExtensions
set "AQUI=%~dp0"

if not exist "%AQUI%respaldo.config" (
    echo No existe respaldo.config. Copia respaldo.config.ejemplo como respaldo.config y ajustalo.
    pause
    exit /b 1
)

set "MYSQL_BIN="
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=PuestoMI"
set "DB_USER=root"
set "DB_PASS="
set "CARPETA_COPIAS="
for /f "usebackq eol=# tokens=1* delims==" %%A in ("%AQUI%respaldo.config") do set "%%A=%%B"

if "%CARPETA_COPIAS%"=="" set "CARPETA_COPIAS=%AQUI%copias"

set "DUMP=mysqldump"
set "CLIENTE=mysql"
if not "%MYSQL_BIN%"=="" (
    set "DUMP=%MYSQL_BIN%\mysqldump.exe"
    set "CLIENTE=%MYSQL_BIN%\mysql.exe"
)
set "MYSQL_PWD=%DB_PASS%"

set "ARCHIVO=%~1"
if not "%ARCHIVO%"=="" goto verificar

echo Respaldos disponibles en %CARPETA_COPIAS%  (el mas reciente primero):
echo.
dir /b /o-d "%CARPETA_COPIAS%\*.sql"
echo.
set /p "NOMBRE=Escribe el nombre del archivo a restaurar (o ENTER para cancelar): "
if "%NOMBRE%"=="" exit /b 0
set "ARCHIVO=%CARPETA_COPIAS%\%NOMBRE%"

:verificar
if not exist "%ARCHIVO%" (
    echo No existe el archivo: %ARCHIVO%
    pause
    exit /b 1
)

echo.
echo ATENCION: se van a reemplazar TODOS los datos actuales de %DB_NAME% por los de:
echo   %ARCHIVO%
echo Todo lo registrado despues de ese respaldo se perdera.
echo Cierra Tomcat / el sistema antes de continuar.
echo.
set /p "CONFIRMA=Para continuar escribe SI en mayusculas: "
if not "%CONFIRMA%"=="SI" (
    echo Cancelado. No se cambio nada.
    pause
    exit /b 0
)

rem Copia de seguridad del estado actual, por si te equivocas de archivo
for /f %%I in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HH-mm"') do set "FECHA=%%I"
if not exist "%CARPETA_COPIAS%" mkdir "%CARPETA_COPIAS%"
set "PREVIO=%CARPETA_COPIAS%\antes_de_restaurar_%FECHA%.sql"
"%DUMP%" --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --single-transaction --routines --triggers --default-character-set=utf8mb4 --result-file="%PREVIO%" %DB_NAME%
if errorlevel 1 (
    echo ERROR: no se pudo guardar la copia previa. No se restauro nada.
    pause
    exit /b 1
)
echo Copia del estado actual guardada en: %PREVIO%

"%CLIENTE%" --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --default-character-set=utf8mb4 %DB_NAME% < "%ARCHIVO%"
if errorlevel 1 (
    echo ERROR: la restauracion fallo. Tus datos de antes estan en: %PREVIO%
    pause
    exit /b 1
)

echo %date% %time% RESTAURADO: %ARCHIVO%>>"%CARPETA_COPIAS%\respaldo.log"
echo Restauracion terminada. Reinicia el sistema.
pause
exit /b 0
