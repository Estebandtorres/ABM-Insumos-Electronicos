import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Conexion {
    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "insumos_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    private static final String URL_SERVIDOR = "jdbc:mysql://" + HOST + ":" + PUERTO
            + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String URL_BASE_DATOS = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public Connection establecerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver de MySQL. Agrega mysql-connector-j al proyecto.", e);
        }

        crearBaseSiNoExiste();
        Connection conexion = DriverManager.getConnection(URL_BASE_DATOS, USUARIO, CLAVE);
        crearTablas(conexion);
        return conexion;
    }

    private void crearBaseSiNoExiste() throws SQLException {
        try (Connection conexion = DriverManager.getConnection(URL_SERVIDOR, USUARIO, CLAVE);
                Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + BASE_DATOS
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
    }

    private void crearTablas(Connection conexion) throws SQLException {
        String sqlInsumos = "CREATE TABLE IF NOT EXISTS insumos ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "codigo VARCHAR(30) NOT NULL UNIQUE, "
                + "nombre VARCHAR(120) NOT NULL, "
                + "descripcion VARCHAR(255), "
                + "precio DECIMAL(10,2) NOT NULL, "
                + "stock INT NOT NULL"
                + ")";

        try (Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate(sqlInsumos);
        }
    }
}
