package model;

import java.math.BigDecimal;

public class Tarifa {
    private int id;
    private String tipoVehiculo;
    private BigDecimal tarifaHora;
    private BigDecimal tarifaDia;

    public Tarifa() {}

    public Tarifa(int id, String tipoVehiculo, BigDecimal tarifaHora, BigDecimal tarifaDia) {
        this.id = id; this.tipoVehiculo = tipoVehiculo; this.tarifaHora = tarifaHora; this.tarifaDia = tarifaDia;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }
    public BigDecimal getTarifaDia() { return tarifaDia; }
    public void setTarifaDia(BigDecimal tarifaDia) { this.tarifaDia = tarifaDia; }
}

