package Panaca.dto.devolucion;

import Panaca.model.devolucion.TipoDevolucion;

public class DevolucionRequestDTO {

    private String cuentaId;         // Antes era Long
    private TipoDevolucion tipo;
    private String referenciaId;

    public String getCuentaId() {
        return cuentaId;
    }
    public void setCuentaId(String cuentaId) {
        this.cuentaId = cuentaId;
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
}
