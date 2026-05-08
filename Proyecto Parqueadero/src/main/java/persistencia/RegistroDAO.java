package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Registro;

public class RegistroDAO {
    private final ConexionDB conexionDB = new ConexionDB();

    public boolean insertarIngreso(Registro r) throws SQLException {
        String sql = "INSERT INTO ingresos_salidas (id_vehiculo, id_espacio, hora_entrada, id_tarifa, en_taller) VALUES (?,?,?,?,?)";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.getIdVehiculo());
            ps.setInt(2, r.getIdEspacio());
            ps.setObject(3, r.getHoraEntrada()); // JDBC soporta LocalDateTime en drivers modernos
            ps.setInt(4, r.getIdTarifa());
            ps.setBoolean(5, r.isEnTaller());
            return ps.executeUpdate() > 0;
        }
    }
}
