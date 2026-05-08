package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConexionDB: usa variables de entorno DB_URL, DB_USER, DB_PASS (con valores por defecto).
 */
public class ConexionDB {

    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://bcofumpvcuweytvnp882-postgresql.services.clever-cloud.com:50013/bcofumpvcuweytvnp882");
    private static final String USUARIO = System.getenv().getOrDefault("DB_USER", "uydgjigjtfin5hsdgxz7");
    private static final String CLAVE = System.getenv().getOrDefault("DB_PASS", "SrB98BpCeudri7lJS5veV1WrjNOFve");
    private static final String DRIVER = "org.postgresql.Driver";

    public Connection conectar() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL no encontrado en el classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    public void desconectar(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al desconectar: " + e.getMessage());
            }
        }
    }
}
