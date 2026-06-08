package model;

public abstract class Persona {
 protected int id;
 protected String nombre;
 protected String apellido;
 protected String telefono;
 protected String clave;

 public Persona(String nombre, String apellido, String telefono) {
  this.nombre = nombre;
  this.apellido = apellido;
  this.telefono = telefono;
 }

 public Persona(int id, String nombre, String apellido, String telefono) {
  this(nombre, apellido, telefono);
  this.id = id;
 }

 public int getId() { return id; }
 public String getNombre() { return nombre; }
 public String getApellido() { return apellido; }
 public String getTelefono() { return telefono; }
 public String getClave() { return clave; }

 public void setId(int id) { this.id = id; }
 public void setNombre(String nombre) { this.nombre = nombre; }
 public void setApellido(String apellido) { this.apellido = apellido; }
 public void setTelefono(String telefono) { this.telefono = telefono; }
 public void setClave(String clave) { this.clave = clave; }
}
