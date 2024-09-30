package co.edu.uniquindio.unieventos.service.service;

import co.edu.uniquindio.unieventos.dto.autenticacion.TokenDTO;
import co.edu.uniquindio.unieventos.dto.cuenta.*;
import co.edu.uniquindio.unieventos.exceptions.CuentaException;
import co.edu.uniquindio.unieventos.model.documents.Cuenta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CuentaService {

    void crearCuenta(CrearCuentaDTO cuenta) throws CuentaException;

    void validarCodigo(ValidarCodigoDTO validarCodigoDTO) throws CuentaException;

    String editarCuenta(EditarCuentaDTO cuenta) throws CuentaException;

    String eliminarCuenta(String id) throws CuentaException;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws CuentaException;

    String enviarCodigoRecuperacionPassword(String correo) throws CuentaException;

    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws CuentaException;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws CuentaException;

    Map<String, Object> construirClaims(Cuenta cuenta);
}
