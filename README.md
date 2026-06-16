# ABM de Insumos Electronicos

Aplicacion de escritorio desarrollada con Java Swing, MySQL y JDBC.

## Funcionalidades

- Alta de insumos.
- Listado en tabla.
- Seleccion de registros desde la tabla.
- Modificacion de insumos.
- Baja de insumos.
- Busqueda por codigo, nombre o descripcion.
- Conexion a MySQL con creacion automatica de la base `insumos_db` y la tabla `insumos`.

## Requisitos

- Java JDK instalado.
- MySQL iniciado.
- Driver JDBC de MySQL: `mysql-connector-j`.

Coloca el archivo `.jar` del conector dentro de la carpeta `lib`.

## Configuracion de base de datos

La conexion se configura en `src/Conexion.java`:

```java
private static final String HOST = "localhost";
private static final String PUERTO = "3306";
private static final String BASE_DATOS = "insumos_db";
private static final String USUARIO = "root";
private static final String CLAVE = "";
```

Si tu MySQL tiene contrasena, cambia `CLAVE`.

## Ejecutar desde terminal

En Windows, usando PowerShell:

```powershell
javac -cp "lib/*" -d bin src/*.java
java -cp "bin;lib/*" App
```

## Criterios cubiertos

- Java Swing: interfaz grafica clara.
- Modelo/Database: conexion MySQL mediante JDBC.
- MVC: modelo `Insumos`, vista `FrmInsumos`, acceso a datos `InsumosDAO` y conexion `Conexion`.
