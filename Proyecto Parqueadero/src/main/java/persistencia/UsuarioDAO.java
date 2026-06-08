package persistencia;

import model.Usuario;

import java.sql.*;

public class UsuarioDAO implements I_CRUD<Usuario> {
 private final ConexionDB conexionDB = new ConexionDB();

 @Override
 public void crear(Usuario u) throws SQLException {
  String sql = "INSERT INTO usuarios (nombre_usuario, apellido_usuario, telefono_usuario, email_usuario, cliente_frecuente) VALUES (?,?,?,?,?)";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
   ps.setString(1, u.getNombre());
   ps.setString(2, u.getApellido());
   ps.setString(3, u.getTelefono());
   ps.setString(4, u.getEmail());
   ps.setBoolean(5, u.isClienteFrecuente());
   ps.executeUpdate();
   try (ResultSet rk = ps.getGeneratedKeys()) {
    if (rk.next()) u.setId(rk.getInt(1));
   }
  }
 }

 @Override
 public Usuario leerPorId(int id) throws SQLException {
  String sql = "SELECT id_usuario, nombre_usuario, apellido_usuario, telefono_usuario, email_usuario, cliente_frecuente FROM usuarios WHERE id_usuario = ?";
  Usuario u = null;
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setInt(1, id);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     u = new Usuario(
       rs.getInt("id_usuario"),
       rs.getString("nombre_usuario"),
       rs.getString("apellido_usuario"),
       rs.getString("telefono_usuario"),
       rs.getString("email_usuario"),
       rs.getBoolean("cliente_frecuente")
     );
    }
   }
  }
  return u;
 }

 @Override
 public void actualizar(Usuario u) throws SQLException {
  String sql = "UPDATE usuarios SET nombre_usuario = ?, apellido_usuario = ?, telefono_usuario = ?, email_usuario = ?, cliente_frecuente = ? WHERE id_usuario = ?";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setString(1, u.getNombre());
   ps.setString(2, u.getApellido());
   ps.setString(3, u.getTelefono());
   ps.setString(4, u.getEmail());
   ps.setBoolean(5, u.isClienteFrecuente());
   ps.setInt(6, u.getId());
   ps.executeUpdate();
  }
 }

 @Override
 public void eliminar(int id) throws SQLException {
  String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setInt(1, id);
   ps.executeUpdate();
  }
 }

 @Override
 public java.util.List<Usuario> leerTodos() throws SQLException {
  java.util.List<Usuario> lista = new java.util.ArrayList<>();
  String sql = "SELECT id_usuario, nombre_usuario, apellido_usuario, telefono_usuario, email_usuario, cliente_frecuente FROM usuarios ORDER BY id_usuario";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql);
   ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    Usuario u = new Usuario(
      rs.getInt("id_usuario"),
      rs.getString("nombre_usuario"),
      rs.getString("apellido_usuario"),
      rs.getString("telefono_usuario"),
      rs.getString("email_usuario"),
      rs.getBoolean("cliente_frecuente")
    );
    lista.add(u);
   }
  }
  return lista;
 }

 /**
  * Valida credenciales de acceso por email y clave.
  * cliente_frecuente = TRUE  -> Administrador
  * cliente_frecuente = FALSE -> Operario/Estándar
  */
 public Usuario validarLogin(String email, String clave) throws SQLException {
  String sql = "SELECT id_usuario, nombre_usuario, apellido_usuario, telefono_usuario, " +
    "email_usuario, cliente_frecuente " +
    "FROM usuarios WHERE email_usuario = ? AND clave = ?";
  try (Connection c = conexionDB.conectar();
   PreparedStatement ps = c.prepareStatement(sql)) {
   ps.setString(1, email);
   ps.setString(2, clave);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     return new Usuario(
       rs.getInt("id_usuario"),
       rs.getString("nombre_usuario"),
       rs.getString("apellido_usuario"),
       rs.getString("telefono_usuario"),
       rs.getString("email_usuario"),
       rs.getBoolean("cliente_frecuente")
     );
    }
   }
  }
  return null;
 }
}
