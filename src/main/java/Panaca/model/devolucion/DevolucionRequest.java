package Panaca.model.devolucion;

import Panaca.model.documents.Cuenta;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DevolucionRequest {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cuenta cuenta;

    @Enumerated(EnumType.STRING)
    private TipoDevolucion tipo;

    /**
     * Para TICKET: aquí va el orderId (String)
     * Para DONACION: aquí va el donationId.toString()
     */
    private String referenciaId;

    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    private EstadoDevolucion estado;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cuenta getCuenta() { return cuenta; }
    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }

    public TipoDevolucion getTipo() { return tipo; }
    public void setTipo(TipoDevolucion tipo) { this.tipo = tipo; }

    public String getReferenciaId() { return referenciaId; }
    public void setReferenciaId(String referenciaId) { this.referenciaId = referenciaId; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public EstadoDevolucion getEstado() { return estado; }
    public void setEstado(EstadoDevolucion estado) { this.estado = estado; }
}

