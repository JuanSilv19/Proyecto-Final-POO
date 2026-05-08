package model;

import java.time.LocalDateTime;

public class Registro {

    private int id;
    private int idVehiculo;
    private int idEspacio;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private int idTarifa;
    private boolean enTaller;

    public Registro(int id, int idVehiculo, int idEspacio, LocalDateTime horaEntrada,
                    LocalDateTime horaSalida, int idTarifa, boolean enTaller) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.idEspacio = idEspacio;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.idTarifa = idTarifa;
        this.enTaller = enTaller;
    }

    // Constructor sin ID para inserts
    public Registro(int idVehiculo, int idEspacio, LocalDateTime horaEntrada,
                    int idTarifa, boolean enTaller) {
        this.idVehiculo = idVehiculo;
        this.idEspacio = idEspacio;
        this.horaEntrada = horaEntrada;
        this.idTarifa = idTarifa;
        this.enTaller = enTaller;
    }

    // GETTERS
    public int getId() { return id; }

    public int getIdVehiculo() { return idVehiculo; }

    public int getIdEspacio() { return idEspacio; }

    public LocalDateTime getHoraEntrada() { return horaEntrada; }

    public LocalDateTime getHoraSalida() { return horaSalida; }

    public int getIdTarifa() { return idTarifa; }

    public boolean isEnTaller() { return enTaller; }

    // SETTERS
    public void setId(int id) { this.id = id; }

    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public void setIdEspacio(int idEspacio) { this.idEspacio = idEspacio; }

    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public void setEnTaller(boolean enTaller) { this.enTaller = enTaller; }
}
