package Panaca.dto.devolucion;

import Panaca.model.devolucion.EstadoDevolucion;
import Panaca.model.devolucion.TipoDevolucion;
import java.time.LocalDateTime;

public class DevolucionResponseDTO {
    private Long id;
    private TipoDevolucion tipo;
    private String referenciaId;          // Cambiado a String
    private LocalDateTime fechaSolicitud;
    private EstadoDevolucion estado;

    public DevolucionResponseDTO(Long id, TipoDevolucion tipo, String referenciaId,
                                 LocalDateTime fechaSolicitud, EstadoDevolucion estado) {
        this.id = id;
        this.tipo = tipo;
        this.referenciaId = referenciaId;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public TipoDevolucion getTipo() {
        return tipo;
    }
    public void setTipo(TipoDevolucion tipo) {
        this.tipo = tipo;
    }

    public String getReferenciaId() {
        return referenciaId;
    }
    public void setReferenciaId(String referenciaId) {
        this.referenciaId = referenciaId;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoDevolucion getEstado() {
        return estado;
    }
    public void setEstado(EstadoDevolucion estado) {
        this.estado = estado;
    }
}
