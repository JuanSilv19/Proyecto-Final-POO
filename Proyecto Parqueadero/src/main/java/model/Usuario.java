package model;

public class Usuario extends Persona {
 private String email;
 private boolean clienteFrecuente;

 public Usuario() {
  super("", "", "");
 }

 public Usuario(String nombre, String apellido, String telefono, String email, boolean clienteFrecuente) {
  super(nombre, apellido, telefono);
  this.email = email;
  this.clienteFrecuente = clienteFrecuente;
 }

 public Usuario(int id, String nombre, String apellido, String telefono, String email, boolean clienteFrecuente) {
  super(id, nombre, apellido, telefono);
  this.email = email;
  this.clienteFrecuente = clienteFrecuente;
 }

 public String getEmail() { return email; }
 public void setEmail(String email) { this.email = email; }
 public boolean isClienteFrecuente() { return clienteFrecuente; }
 public void setClienteFrecuente(boolean clienteFrecuente) { this.clienteFrecuente = clienteFrecuente; }
}
