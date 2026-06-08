package persistencia;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Registro;
import model.Vehiculo;

public class RegistroDAO {
 private final ConexionDB conexionDB = new ConexionDB();

 public boolean insertarIngreso(Registro r) throws SQLException {
  String sql = "INSERT INTO ingresos_salidas (id_vehiculo, id_espacio, hora_entrada, id_tarifa, en_taller) VALUES (?,?,?,?,?)";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setInt(1, r.getIdVehiculo());
   ps.setInt(2, r.getIdEspacio());
   ps.setObject(3, r.getHoraEntrada());
   ps.setInt(4, r.getIdTarifa());
   ps.setBoolean(5, r.isEnTaller());
   return ps.executeUpdate() > 0;
  }
 }

 public List<Object[]> getRegistrosActivosConDetalles() throws SQLException {
  List<Object[]> resultados = new ArrayList<>();
  String sql = "SELECT v.placa, DATE(i.hora_entrada) as fecha_entrada, " +
    "TIME(i.hora_entrada) as hora_entrada, v.tipo as tipo_vehiculo, " +
    "i.id_ingreso_salida as numero_recibo " +
    "FROM ingresos_salidas i " +
    "JOIN vehiculos v ON i.id_vehiculo = v.id_vehiculo " +
    "WHERE i.hora_salida IS NULL " +
    "ORDER BY i.hora_entrada DESC";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql);
   ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    Object[] fila = new Object[5];
    fila[0] = rs.getString("placa");
    fila[1] = rs.getDate("fecha_entrada");
    fila[2] = rs.getTime("hora_entrada");
    fila[3] = rs.getString("tipo_vehiculo");
    fila[4] = rs.getInt("numero_recibo");
    resultados.add(fila);
   }
  }
  return resultados;
 }

 public Registro getRegistroById(int id) throws SQLException {
  String sql = "SELECT i.*, v.placa, v.tipo_vehiculo as tipoVehiculo " +
    "FROM ingresos_salidas i " +
    "JOIN vehiculos v ON i.id_vehiculo = v.id_vehiculo " +
    "WHERE i.id_ingreso_salida = ?";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setInt(1, id);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     Registro registro = new Registro();
     registro.setId(rs.getInt("id_ingreso_salida"));
     registro.setIdVehiculo(rs.getInt("id_vehiculo"));
     registro.setIdEspacio(rs.getInt("id_espacio"));
     registro.setHoraEntrada(rs.getObject("hora_entrada", LocalDateTime.class));
     registro.setHoraSalida(rs.getObject("hora_salida", LocalDateTime.class));
     registro.setIdTarifa(rs.getInt("id_tarifa"));
     registro.setEnTaller(rs.getBoolean("en_taller"));
     registro.setPlaca(rs.getString("placa"));
     registro.setTipoVehiculo(rs.getString("tipoVehiculo"));
     return registro;
    }
   }
  }
  return null;
 }

 /**
  * Busca el registro ACTIVO de un vehículo por su placa.
  * @param placa Placa del vehículo a buscar
  * @return Registro activo encontrado o null si no existe
  */
 public Registro getRegistroByPlaca(String placa) throws SQLException {
  String sql = "SELECT i.*, v.placa, v.tipo_vehiculo as tipoVehiculo " +
    "FROM ingresos_salidas i " +
    "JOIN vehiculos v ON i.id_vehiculo = v.id_vehiculo " +
    "WHERE v.placa = ? AND i.hora_salida IS NULL " +
    "ORDER BY i.hora_entrada DESC LIMIT 1";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setString(1, placa.toUpperCase());
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     Registro registro = new Registro();
     registro.setId(rs.getInt("id_ingreso_salida"));
     registro.setIdVehiculo(rs.getInt("id_vehiculo"));
     registro.setIdEspacio(rs.getInt("id_espacio"));
     registro.setHoraEntrada(rs.getObject("hora_entrada", LocalDateTime.class));
     registro.setHoraSalida(rs.getObject("hora_salida", LocalDateTime.class));
     registro.setIdTarifa(rs.getInt("id_tarifa"));
     registro.setEnTaller(rs.getBoolean("en_taller"));
     registro.setPlaca(rs.getString("placa"));
     registro.setTipoVehiculo(rs.getString("tipoVehiculo"));
     return registro;
    }
   }
  }
  return null;
 }

 public boolean actualizarSalida(int id, LocalDateTime horaSalida, double montoPagado) throws SQLException {
  String sql = "UPDATE ingresos_salidas SET hora_salida = ?, monto_pagado = ? WHERE id_ingreso_salida = ?";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setObject(1, horaSalida);
   ps.setBigDecimal(2, java.math.BigDecimal.valueOf(montoPagado));
   ps.setInt(3, id);
   return ps.executeUpdate() > 0;
  }
 }

 public int contarVehiculosPorTipo(String tipo) throws SQLException {
  String sql = "SELECT COUNT(*) FROM ingresos_salidas i " +
    "JOIN vehiculos v ON i.id_vehiculo = v.id_vehiculo " +
    "WHERE i.hora_salida IS NULL AND v.tipo = ?";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setString(1, tipo);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     return rs.getInt(1);
    }
   }
  }
  return 0;
 }
}
