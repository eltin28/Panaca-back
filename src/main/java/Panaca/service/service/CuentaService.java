package Panaca.service.service;

import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.cuenta.*;
import Panaca.model.documents.Cuenta;
import Panaca.dto.cuenta.*;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.CuentaException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface CuentaService {

    void crearCuenta(CrearCuentaDTO cuenta) throws CuentaException, CarritoException;

    ValidarCodigoDTO validarCodigo(ValidarCodigoDTO validarCodigoDTO) throws CuentaException;

    void editarCuenta(EditarCuentaDTO cuenta) throws CuentaException;

    void eliminarCuenta(String id) throws CuentaException;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws CuentaException;

    void enviarCodigoRecuperacionPassword(CodigoContraseniaDTO codigoContraseniaDTO) throws CuentaException;

    void cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws CuentaException;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws CuentaException;

    Map<String, Object> construirClaims(Cuenta cuenta);
}
