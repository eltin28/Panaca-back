package co.edu.uniquindio.unieventos.service.implement;

import co.edu.uniquindio.unieventos.configs.JWTUtils;
import co.edu.uniquindio.unieventos.dto.autenticacion.TokenDTO;
import co.edu.uniquindio.unieventos.dto.carrito.CrearCarritoDTO;
import co.edu.uniquindio.unieventos.dto.cuenta.*;
import co.edu.uniquindio.unieventos.dto.email.EmailDTO;
import co.edu.uniquindio.unieventos.exceptions.CarritoException;
import co.edu.uniquindio.unieventos.exceptions.CuentaException;
import co.edu.uniquindio.unieventos.model.documents.Cuenta;
import co.edu.uniquindio.unieventos.model.enums.EstadoCuenta;
import co.edu.uniquindio.unieventos.model.enums.Rol;
import co.edu.uniquindio.unieventos.model.vo.CodigoValidacion;
import co.edu.uniquindio.unieventos.model.vo.Usuario;
import co.edu.uniquindio.unieventos.repository.CuentaRepository;
import co.edu.uniquindio.unieventos.service.service.CarritoService;
import co.edu.uniquindio.unieventos.service.service.CuentaService;
import co.edu.uniquindio.unieventos.service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CuentaServiceImp implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final CarritoService carritoService;
    private final JWTUtils jwtUtils;
    private final EmailService emailService;


    // Metodo de apoyo para buscar una cuenta por ID y devolver un Optional<Cuenta>
    private Optional<Cuenta> obtenerCuentaPorId(String idCuenta) {
        return cuentaRepository.findById(idCuenta);
    }

    /**
     * Metodo para crear una cuenta
     * @param cuentaDTO
     * @throws CuentaException si la cuenta validada por el email ya existe
     */
    @Override
    public void crearCuenta(CrearCuentaDTO cuentaDTO) throws CuentaException, CarritoException {
        // Verifica si ya existe una cuenta con el mismo email
        if (cuentaRepository.existsByEmail(cuentaDTO.email())) {
            throw new CuentaException("Ya existe una cuenta registrada con este email electrónico.");
        }
        if(cuentaRepository.equals(cuentaDTO.cedula())){
            throw new CuentaException("Ya existe una cuenta registrada con esta cedula.");
        }

        // Crear instancia de Cuenta con datos del DTO
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setEmail(cuentaDTO.email());
        nuevaCuenta.setPassword(cuentaDTO.password());
        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setRol(Rol.CLIENTE);
        nuevaCuenta.setUsuario(new Usuario(
                cuentaDTO.cedula(),
                cuentaDTO.nombre(),
                cuentaDTO.telefono(),
                cuentaDTO.direccion()
        ));
        nuevaCuenta.setEstado(EstadoCuenta.INACTIVO);

        String encrptar = encriptarPassword(cuentaDTO.password());
        nuevaCuenta.setPassword(encrptar);

        // Generar y asignar el código de validación
        String codigoValidacion = generarCodigoValidacion();
        nuevaCuenta.setCodigoVerificacionRegistro(codigoValidacion);

        // Guardar la cuenta en la base de datos
        Cuenta cuentaCreada = cuentaRepository.save(nuevaCuenta);

        // Lógica para crear el carrito automáticamente
        CrearCarritoDTO carritoDTO = new CrearCarritoDTO(
                cuentaCreada.getId(),
                new ArrayList<>(),
                LocalDateTime.now()
        );
        carritoService.crearCarrito(carritoDTO);

        // Enviar el código de validación al correo
        String asunto = "Código de Validación";
        String cuerpo = "Tu código de validación es: " + codigoValidacion;
        EmailDTO emailDTO = new EmailDTO(asunto, cuerpo, cuentaDTO.email());
        emailService.enviarCorreo(emailDTO);
    }

    /**
     * Metodo para validar que el codigo de el usuario sea el enviado a el correo
     *
     * @param email
     * @param codigo
     * @return
     * @throws CuentaException
     */
    @Override
    public ValidarCodigoDTO validarCodigo(ValidarCodigoDTO validarCodigoDTO) throws CuentaException {
        // Buscar la cuenta por email
        Cuenta cuenta = cuentaRepository.findByEmail(validarCodigoDTO.email())
                .orElseThrow(() -> new CuentaException("No se encontró la cuenta."));

        // Comparar el código enviado con el código almacenado
        if (!cuenta.getCodigoVerificacionRegistro().equals(validarCodigoDTO.codigo())) {
            throw new CuentaException("El código de verificación es incorrecto.");
        }

        // Si el código es correcto, activar la cuenta
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setCodigoVerificacionRegistro(null);
        cuentaRepository.save(cuenta);

        return validarCodigoDTO;
    }

    // Metoodo para generar un código de validación de 4 cifras
    private String generarCodigoValidacion() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }

    //Metodo para encriptar la contraseña
    private String encriptarPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode( password );
    }

    /**
     * Metodo para editar los datos de una cuenta
     * @param cuenta
     * @return Mensaje de exito de que los fueros modificados
     * @throws CuentaException
     */
    @Override
    public void editarCuenta(EditarCuentaDTO cuentaEditada) throws CuentaException {
        //Buscamos la cuenta del usuario que se quiere actualizar
        Optional<Cuenta> optionalCuenta = obtenerCuentaPorId(cuentaEditada.id());

        //Si no se encontró la cuenta del usuario, lanzamos una excepción
        if(optionalCuenta.isEmpty()){
            throw new CuentaException("No existe un usuario con el id dado");
        }

        //Obtenemos la cuenta del usuario a modificar y actualizamos sus datos
        Cuenta cuentaModificada = optionalCuenta.get();
        cuentaModificada.getUsuario().setNombre( cuentaEditada.nombre() );
        cuentaModificada.getUsuario().setTelefono( cuentaEditada.telefono() );
        cuentaModificada.getUsuario().setDireccion( cuentaEditada.direccion() );
        // Actualizar la contraseña si se proporciona
        if (cuentaEditada.password() != null && !cuentaEditada.password().isEmpty()) {
            String nuevaPassword = cuentaEditada.password();
            cuentaModificada.setPassword( cuentaEditada.password() );
            String encrptar = encriptarPassword(cuentaEditada.password());
            cuentaModificada.setPassword(encrptar);
        }

        //Como el objeto cuenta ya tiene un id, el save() no crea un nuevo registro sino que actualiza el que ya existe
        cuentaRepository.save(cuentaModificada);
    }

    /**
     * Metodo para eliminar una cuenta por su ID
     * @param id
     * @return el id de la cuenta eliminada
     * @throws CuentaException
     */
    @Override
    public void eliminarCuenta(String id) throws CuentaException {
        //Buscamos la cuenta del usuario que se quiere eliminar
        Optional<Cuenta> optionalCuenta = obtenerCuentaPorId(id);

        //Si no se encontró la cuenta, lanzamos una excepción
        if(optionalCuenta.isEmpty()){
            throw new CuentaException("No se encontró el usuario con el id "+id);
        }

        //Obtenemos la cuenta del usuario que se quiere eliminar y le asignamos el estado eliminado
        Cuenta cuenta = optionalCuenta.get();
        cuenta.setEstado(EstadoCuenta.ELIMINADO);

        cuentaRepository.save(cuenta);
    }

    /**
     * Metodo para obtener la informacion de una cuenta
     * @param id
     * @return los datos obtenidos de una cuenta por su id
     * @throws CuentaException
     */
    @Override
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws CuentaException {
        System.out.println("Buscando cuenta con ID: " + id);
        Optional<Cuenta> optionalCuenta = obtenerCuentaPorId(id);

        if (optionalCuenta.isEmpty()) {
            System.out.println("No se encontró la cuenta con ID: " + id);
            throw new CuentaException("No se encontró el usuario con el id " + id);
        }

        Cuenta cuenta = optionalCuenta.get();
        System.out.println("Cuenta encontrada: " + cuenta);

        return new InformacionCuentaDTO(
                cuenta.getId(),
                cuenta.getUsuario().getCedula(),
                cuenta.getUsuario().getNombre(),
                cuenta.getUsuario().getTelefono(),
                cuenta.getUsuario().getDireccion(),
                cuenta.getEmail()
        );
    }

    /**
     * Metodo para enviar los codigos de recuperacion para un contraseña
     * @param correo
     * @return
     * @throws CuentaException
     */
    @Override
    public void enviarCodigoRecuperacionPassword(CodigoContraseniaDTO codigoContrasenieDTO) throws CuentaException {

        // Buscar la cuenta en la base de datos usando el email proporcionado
        Cuenta cuentaExistente = cuentaRepository.findByEmail(codigoContrasenieDTO.email())
                .orElseThrow(() -> new CuentaException("Cuenta no encontrada para el email proporcionado"));

        // Generar un código de recuperación único
        String codigoRecuperacion = generarCodigoValidacion();

        // Guardar el código en la cuenta (o en un lugar seguro relacionado con la cuenta)
        cuentaExistente.setCodigoVerificacionContrasenia(codigoRecuperacion);
        cuentaRepository.save(cuentaExistente);

        // Enviar el código de validación al correo
        String asunto = "Código de recuperación";
        String cuerpo = "El código para recuperar tu contraseña es: " + codigoRecuperacion;
        EmailDTO emailDTO = new EmailDTO(asunto, cuerpo, codigoContrasenieDTO.email());
        emailService.enviarCorreo(emailDTO);
    }

    @Override
    public void cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws CuentaException {
        // Buscar la cuenta que contiene el código de verificación
        Optional<Cuenta> optionalCuenta = cuentaRepository.existBycodigoVerificacionContrasenia(cambiarPasswordDTO.codigoVerificacion());

        // Si la cuenta no existe, lanzar excepción
        Cuenta cuenta = optionalCuenta.orElseThrow(() -> new CuentaException("Código de verificación inválido o expirado."));

        // Verificar que el código de verificación sea el mismo que el enviado
        if (!cuenta.getCodigoVerificacionContrasenia().equals(cambiarPasswordDTO.codigoVerificacion())) {
            throw new CuentaException("Código de verificación inválido.");
        }

        // Actualizar la contraseña con la nueva contraseña proporcionada
        cuenta.setPassword(cambiarPasswordDTO.passwordNueva());
        String encrptar = encriptarPassword(cambiarPasswordDTO.passwordNueva());
        cuenta.setPassword(encrptar);

        // Limpiar el código de verificación después de cambiar la contraseña
        cuenta.setCodigoVerificacionContrasenia(null);

        // Guardar los cambios en la base de datos
        cuentaRepository.save(cuenta);
    }

    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO)  throws CuentaException {
        // Buscar la cuenta por email electrónico
        Optional<Cuenta> optionalCuenta = cuentaRepository.findByEmail(loginDTO.email());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Si la cuenta no existe, lanzar excepción
        Cuenta cuenta = optionalCuenta.orElseThrow(() -> new CuentaException("Correo electrónico o contraseña incorrectos."));

        if( !passwordEncoder.matches(loginDTO.password(), cuenta.getPassword()) ) {
            throw new CuentaException("La contraseña es incorrecta");
        }

        // Verificar el estado de la cuenta (debe estar activa para iniciar sesión)
        if (!cuenta.getEstado().equals(EstadoCuenta.ACTIVO)) {
            throw new CuentaException("La cuenta no está activa.");
        }

        Map<String, Object> map = construirClaims(cuenta);
        return new TokenDTO( jwtUtils.generarToken(cuenta.getEmail(), map) );
    }

    @Override
    public Map<String, Object> construirClaims(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }





}
