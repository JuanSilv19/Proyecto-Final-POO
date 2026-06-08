package model;

import java.time.LocalDateTime;

/**
 * Clase que representa un registro de ingreso/salida de vehículo
 * Incluye campos adicionales placa y tipoVehiculo para facilitar la consulta JOIN
 */
public class Registro {

    // Constructor vacío para instanciación por defecto (usado por RegistroDAO)
    public Registro() { }

    private int id;
    private int idVehiculo;
    private int idEspacio;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private int idTarifa;
    private boolean enTaller;

    // Campos adicionales para consultas JOIN (no forman parte de la tabla base)
    private String placa;
    private String tipoVehiculo;

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

    // GETTERS para campos adicionales (placa y tipoVehiculo)
    public String getPlaca() { return placa; }
    public String getTipoVehiculo() { return tipoVehiculo; }

    // SETTERS
    public void setId(int id) { this.id = id; }

    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public void setIdEspacio(int idEspacio) { this.idEspacio = idEspacio; }

    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }

    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public void setEnTaller(boolean enTaller) { this.enTaller = enTaller; }

    // SETTERS para campos adicionales
    public void setPlaca(String placa) { this.placa = placa; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
}
