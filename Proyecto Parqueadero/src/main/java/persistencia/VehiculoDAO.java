package persistencia;

import model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO implements I_CRUD<Vehiculo> {
    private final ConexionDB conexionDB = new ConexionDB();

    @Override
    public void crear(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (id_usuario, placa, tipo_vehiculo) VALUES (?,?,?)";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (v.getIdUsuario() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, v.getIdUsuario());
            ps.setString(2, v.getPlaca());
            ps.setString(3, v.getTipo());
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) { if (rk.next()) v.setId(rk.getInt(1)); }
        }
    }

    @Override
    public Vehiculo leerPorId(int id) throws SQLException {
        String sql = "SELECT id_vehiculo, id_usuario, placa, tipo_vehiculo FROM vehiculos WHERE id_vehiculo = ?";
        Vehiculo v = null;
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    v = new Vehiculo();
                    v.setId(rs.getInt("id_vehiculo"));
                    int uid = rs.getInt("id_usuario");
                    if (rs.wasNull()) v.setIdUsuario(null); else v.setIdUsuario(uid);
                    v.setPlaca(rs.getString("placa"));
                    v.setTipo(rs.getString("tipo_vehiculo"));
                }
            }
        }
        return v;
    }

    @Override
    public void actualizar(Vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET id_usuario = ?, placa = ?, tipo_vehiculo = ? WHERE id_vehiculo = ?";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (v.getIdUsuario() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, v.getIdUsuario());
            ps.setString(2, v.getPlaca());
            ps.setString(3, v.getTipo());
            ps.setInt(4, v.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM vehiculos WHERE id_vehiculo = ?";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Vehiculo> leerTodos() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT id_vehiculo, id_usuario, placa, tipo_vehiculo FROM vehiculos ORDER BY id_vehiculo";
        try (Connection c = conexionDB.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vehiculo v = new Vehiculo();
                v.setId(rs.getInt("id_vehiculo"));
                int uid = rs.getInt("id_usuario");
                if (rs.wasNull()) v.setIdUsuario(null); else v.setIdUsuario(uid);
                v.setPlaca(rs.getString("placa"));
                v.setTipo(rs.getString("tipo_vehiculo"));
                lista.add(v);
            }
        }
        return lista;
    }
}