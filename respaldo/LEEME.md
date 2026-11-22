# Respaldo de la base de datos PuestoMI

Dos archivos, cada uno hace una sola cosa:

| Archivo | Qué hace | ¿Modifica la base? |
|---|---|---|
| `respaldo.bat` | Exporta toda la base a `copias\PuestoMI_AAAA-MM-DD_HH-mm.sql` y borra los respaldos de más de 30 días | **No**, solo lee |
| `restaurar.bat` | Vuelve a cargar un respaldo | **Sí**: reemplaza los datos actuales |

Cada integrante del equipo trabaja con **su propia** base en su PC. Estos scripts solo actúan sobre el MySQL de la PC donde se ejecutan; no afectan la base de nadie más. La contraseña va en `respaldo.config`, que no se sube a GitHub.

## Preparación (una sola vez)

1. Copia `respaldo.config.ejemplo` como `respaldo.config` (misma carpeta).
2. Ábrelo con el Bloc de notas y ajusta:
   - `MYSQL_BIN`: carpeta donde está `mysqldump.exe` (normalmente `C:\Program Files\MySQL\MySQL Server 8.0\bin`; si no sabes, busca `mysqldump.exe` en el Explorador).
   - `DB_USER` y `DB_PASS`: los mismos de `persistence.xml`.
   - `CARPETA_COPIAS`: opcional (quita el `#` de esa línea para usarla). Lo ideal es otra unidad (USB, disco externo) o una carpeta sincronizada con la nube.
3. Prueba: doble clic en `respaldo.bat`. Debe decir "Respaldo listo" y aparecer el `.sql` en la carpeta `copias`.

## Respaldo automático diario (Programador de tareas de Windows)

1. Abre el menú Inicio, escribe **Programador de tareas** y ábrelo.
2. Panel derecho → **Crear tarea…** (no "tarea básica").
3. Pestaña **General**: nombre `Respaldo PuestoMI`.
4. Pestaña **Desencadenadores** → **Nuevo…** → *Diariamente*, a la hora en que ya cerraste el puesto (por ejemplo 10:00 p. m.) → Aceptar.
5. Pestaña **Acciones** → **Nueva…** → *Iniciar un programa*. En **Programa o script** elige `respaldo.bat` (botón Examinar). En **Agregar argumentos** escribe `auto` (así no se queda esperando una tecla).
6. Pestaña **Configuración**: marca **Ejecutar la tarea lo antes posible si se omitió un inicio programado**. Así, si la PC estaba apagada a esa hora, el respaldo se hace al encenderla.
7. Aceptar. Para probar: clic derecho sobre la tarea → **Ejecutar**, y revisa que aparezca un `.sql` nuevo.

El archivo `copias\respaldo.log` guarda el resultado de cada ejecución (OK o ERROR).

## Restaurar (solo en emergencia o al cambiar de PC)

1. **Cierra Tomcat / el sistema.**
2. Doble clic en `restaurar.bat`, elige el archivo de la lista y escribe `SI`.
3. El script primero guarda el estado actual como `copias\antes_de_restaurar_FECHA.sql`; si te equivocaste de archivo, restaura ese.
4. Reinicia el sistema.

## Recomendaciones

- Prueba `restaurar.bat` una vez con un respaldo real **antes** de necesitarlo, de preferencia en otra PC o con una base de prueba.
- Copia la carpeta `copias` a una USB o a la nube de vez en cuando: un respaldo en el mismo disco no te protege si el disco falla.
- Los `.sql` contienen todos los datos del negocio (y los hashes/contraseñas de usuarios): guárdalos solo donde confíes. El cifrado de respaldos se trata en #36.
- Si la contraseña de MySQL lleva `%` o `!`, puede fallar en el `.bat`; cámbiala por una sin esos caracteres.
