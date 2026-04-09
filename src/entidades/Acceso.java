package entidades;

import java.time.LocalDateTime;

public class Acceso {
    private String idUsuario;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;

    public Acceso(String idUsuario, LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida) {
        this.idUsuario = idUsuario;
        this.fechaHoraEntrada = fechaHoraEntrada;
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    @Override
    public String toString() {
        return "Acceso de " + idUsuario
                + " | entrada: " + fechaHoraEntrada
                + " | salida: " + (fechaHoraSalida == null ? "Pendiente" : fechaHoraSalida);
    }
}
