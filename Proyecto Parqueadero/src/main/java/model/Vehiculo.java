package model;

public class Vehiculo {
    private int id;
    private Integer idUsuario; // nullable
    private String placa;
    private String tipo; // CARRO, MOTO, MCNC

    public Vehiculo() {}

    public Vehiculo(int id, Integer idUsuario, String placa, String tipo) {
        this.id = id; this.idUsuario = idUsuario; this.placa = placa; this.tipo = tipo;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
