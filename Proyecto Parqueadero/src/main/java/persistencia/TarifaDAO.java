package persistencia;

import model.Tarifa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifaDAO implements I_CRUD<Tarifa> {
    private final ConexionDB conexionDB = new ConexionDB();

    @Override
    public void crear(Tarifa t) throws SQLException {
        String sql = "INSERT INTO tarifas (tipo_vehiculo, tarifa_hora, tarifa_dia) VALUES (?,?,?)";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTipoVehiculo());
            ps.setBigDecimal(2, t.getTarifaHora());
            ps.setBigDecimal(3, t.getTarifaDia());
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) t.setId(rk.getInt(1)); }
        }
    }

    @Override
    public Tarifa leerPorId(int id) throws SQLException {
        String sql = "SELECT id_tarifa, tipo_vehiculo, tarifa_hora, tarifa_dia FROM tarifas WHERE id_tarifa = ?";
        Tarifa t = null;
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = new Tarifa();
                    t.setId(rs.getInt("id_tarifa"));
                    t.setTipoVehiculo(rs.getString("tipo_vehiculo"));
                    t.setTarifaHora(rs.getBigDecimal("tarifa_hora"));
                    t.setTarifaDia(rs.getBigDecimal("tarifa_dia"));
                }
            }
        }
        return t;
    }

    @Override
    public void actualizar(Tarifa t) throws SQLException {
        String sql = "UPDATE tarifas SET tipo_vehiculo = ?, tarifa_hora = ?, tarifa_dia = ? WHERE id_tarifa = ?";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getTipoVehiculo());
            ps.setBigDecimal(2, t.getTarifaHora());
            ps.setBigDecimal(3, t.getTarifaDia());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM tarifas WHERE id_tarifa = ?";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Tarifa> leerTodos() throws SQLException {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT id_tarifa, tipo_vehiculo, tarifa_hora, tarifa_dia FROM tarifas ORDER BY id_tarifa";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarifa t = new Tarifa();
                t.setId(rs.getInt("id_tarifa"));
                t.setTipoVehiculo(rs.getString("tipo_vehiculo"));
                t.setTarifaHora(rs.getBigDecimal("tarifa_hora"));
                t.setTarifaDia(rs.getBigDecimal("tarifa_dia"));
                lista.add(t);
            }
        }
        return lista;
    }
}