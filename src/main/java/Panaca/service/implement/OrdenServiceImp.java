package Panaca.service.implement;

import Panaca.configs.MercadoPagoProperties;
import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.orden.*;
import Panaca.exceptions.*;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Cupon;
import Panaca.model.documents.Evento;
import Panaca.model.documents.Orden;
import Panaca.model.vo.DetalleCarrito;
import Panaca.model.vo.DetalleOrden;
import Panaca.model.vo.Pago;
import Panaca.service.service.*;
import Panaca.repository.CarritoRepository;
import Panaca.repository.OrdenRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdenServiceImp implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final EventoService eventoService;
    private final CarritoRepository carritoRepository;
    private final CuentaService cuentaService;
    private final CuponService cuponService;
    private final MercadoPagoProperties mercadoPagoProperties;

    @Override
    public void crearOrdenDesdeCarrito(String idCliente, String codigoCupon, String codigoPasarela) throws OrdenException, CarritoException, CuponException {

        // Obtener el carrito del cliente, utilizando Optional
        Carrito carrito = carritoRepository.findByIdUsuario(idCliente)
                .orElseThrow(() -> new CarritoException("No se encontró el carrito para el usuario con ID: " + idCliente));

        // Verificar que el carrito tenga ítems
        if (carrito.getItems().isEmpty()) {
            throw new OrdenException("El carrito debe tener al menos un detalle.");
        }

        // Convertir los detalles del carrito a detalles de la orden
        List<DetalleOrdenDTO> detallesOrdenDTO = convertirCarritoADetalleOrdenDTO(carrito.getItems());

        for (DetalleOrdenDTO detalle : detallesOrdenDTO) {
            if (detalle.fechaUso().isBefore(LocalDate.now())) {
                throw new OrdenException("La fecha de uso no puede estar en el pasado para el evento: " + detalle.idEvento());
            }
        }

        // Calcular el total de la orden
        double totalOrden = calcularTotalOrden(detallesOrdenDTO, codigoCupon, LocalDateTime.now());

        // Crear el DTO de la orden usando los datos del usuario
        CrearOrdenDTO ordenDTO = new CrearOrdenDTO(
                idCliente,
                codigoCupon,  // El cupón ingresado por el usuario
                codigoPasarela,  // El código de pasarela ingresado por el usuario
                LocalDate.now(),
                detallesOrdenDTO
        );

        // Llamar al método que crea la orden
        crearOrden(ordenDTO, totalOrden);
    }

    // Convierte los ítems del carrito a DetalleOrdenDTO
    private List<DetalleOrdenDTO> convertirCarritoADetalleOrdenDTO(List<DetalleCarrito> itemsCarrito) {
        return itemsCarrito.stream()
                .map(item -> new DetalleOrdenDTO(
                        item.getIdEvento(),
                        item.getCantidad(),
                        item.getFechaUso()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void crearOrden(CrearOrdenDTO ordenDTO, double totalOrden) throws OrdenException {

        if (ordenDTO.detalleOrden().isEmpty()) {
            throw new OrdenException("La orden debe tener al menos un detalle.");
        }

        // Crear la instancia de Orden usando el DTO
        Orden orden = new Orden();
        orden.setIdCliente(new ObjectId(ordenDTO.idCliente()));
        orden.setCodigoCupon(ordenDTO.codigoCupon() != null ? (ordenDTO.codigoCupon()) : null);
        orden.setFecha(LocalDate.now());
        orden.setCodigoPasarela(ordenDTO.codigoPasarela());

        // Convertir DetalleOrdenDTO a DetalleOrden y asignarlos a la orden
        List<DetalleOrden> detallesOrden = convertirDetalleDTOADetalle(ordenDTO.detalleOrden());
        orden.setDetalle(detallesOrden);

        // Asignar el total calculado a la orden
        orden.setTotal(totalOrden);

        // Guardar la orden en el repositorio
        ordenRepository.save(orden);
    }


    // Convierte los DetalleOrdenDTO a DetalleOrden
    private List<DetalleOrden> convertirDetalleDTOADetalle(List<DetalleOrdenDTO> detalleOrdenDTO) {
        return detalleOrdenDTO.stream()
                .map(dto -> new DetalleOrden(
                        new ObjectId(dto.idEvento()),
                        dto.cantidad(),
                        dto.fechaUso()
                        ))
                .collect(Collectors.toList());
    }

    @Override
    public MostrarOrdenDTO mostrarOrden(String idOrden) throws OrdenException, CuentaException {
        Orden orden = obtenerOrdenPorId(idOrden);
        InformacionCuentaDTO usuario = cuentaService.obtenerInformacionCuenta(orden.getIdCliente().toString());

        List<MostrarDetalleOrdenDTO> detalles = new ArrayList<>();

        for (DetalleOrden detalle : orden.getDetalle()) {
            Evento evento = eventoService.obtenerInformacionEvento(detalle.getIdEvento().toString());
            detalles.add(new MostrarDetalleOrdenDTO(
                    evento.getId(),
                    evento.getNombre(),
                    evento.getTipo(),
                    detalle.getFechaUso(),
                    evento.getPrecio(),
                    detalle.getCantidad()
            ));
        }

        return new MostrarOrdenDTO(
                usuario.nombre(),
                orden.getFecha(),
                orden.getCodigoCupon(),
                orden.getTotal(),
                detalles
        );
    }

    private double calcularTotalOrden(List<DetalleOrdenDTO> detallesOrdenDTO, String codigoCupon, LocalDateTime fechaCompra) throws CuponException {
        double total = 0.0;

        for (DetalleOrdenDTO detalle : detallesOrdenDTO) {
            Evento evento = eventoService.obtenerInformacionEvento(detalle.idEvento());
            total += evento.getPrecio() * detalle.cantidad();
        }

        if (codigoCupon != null && !codigoCupon.isEmpty()) {
            Cupon cupon = cuponService.aplicarCupon(codigoCupon, fechaCompra);
            double descuento = total * (cupon.getDescuento() / 100.0);
            total -= descuento;
        }

        return total;
    }


    // Obtener una orden por ID
    @Override
    public Orden obtenerOrdenPorId(String id) throws OrdenException {
        return ordenRepository.findById(id).orElseThrow(() -> new OrdenException("Orden no encontrada"));
    }

    @Override
    public void actualizarOrden(String id, EditarOrdenDTO ordenDTO) throws OrdenException, CuponException {
        Orden orden = obtenerOrdenPorId(id);

        if (orden == null) {
            throw new OrdenException("No existe una orden con el id dado");
        }

        orden.setCodigoCupon(ordenDTO.codigoCupon());

        // Convertir los nuevos detalles y establecerlos
        List<DetalleOrden> nuevosDetalles = convertirDetalleDTOADetalle(ordenDTO.detalleOrden());
        orden.setDetalle(nuevosDetalles);

        // Recalcular total con los nuevos detalles
        double total = calcularTotalOrden(ordenDTO.detalleOrden(), ordenDTO.codigoCupon(), LocalDateTime.now());
        orden.setTotal(total);

        ordenRepository.save(orden);
    }

    @Override
    public void eliminarOrden(String id) throws OrdenException {
        Optional<Orden> optionalOrden = ordenRepository.findById(id);

        if (optionalOrden.isEmpty()) {
            throw new OrdenException("No se encontró la orden con el ID: " + id);
        }

        ordenRepository.delete(optionalOrden.get());
    }

    @Override
    public Preference realizarPago(String idOrden) throws Exception {
        Orden ordenGuardada = obtenerOrdenPorId(idOrden);
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        for (DetalleOrden item : ordenGuardada.getDetalle()) {
            Evento evento = eventoService.obtenerInformacionEvento(item.getIdEvento().toString());

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(evento.getId())
                    .title(evento.getNombre())
                    .pictureUrl(evento.getImagenPortada())
                    .categoryId(evento.getTipo().name())
                    .quantity(item.getCantidad())
                    .currencyId("COP")
                    .unitPrice(BigDecimal.valueOf(evento.getPrecio()))
                    .build();

            itemsPasarela.add(itemRequest);
        }

        // token desde config
        MercadoPagoConfig.setAccessToken(mercadoPagoProperties.getToken());

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://tusitio.com/pago/exito")
                .failure("https://tusitio.com/pago/fallo")
                .pending("https://tusitio.com/pago/pendiente")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .backUrls(backUrls)
                .items(itemsPasarela)
                .metadata(Map.of("id_orden", ordenGuardada.getId()))
                .notificationUrl("https://de93-2800-e2-9880-a18-6405-c7c4-bed9-4dde.ngrok-free.app")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        ordenGuardada.setCodigoPasarela(preference.getId());
        ordenRepository.save(ordenGuardada);

        return preference;
    }

    @Override
    public void recibirNotificacionMercadoPago(Map<String, Object> request) {
        try {
            if (!"payment".equals(request.get("type"))) return;

            // Validar existencia del campo "data"
            Object data = request.get("data");
            if (!(data instanceof Map)) {
                throw new IllegalArgumentException("Notificación inválida: 'data' no encontrada o malformada.");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) data;

            Object idPagoObj = dataMap.get("id");
            if (idPagoObj == null) {
                throw new IllegalArgumentException("Notificación inválida: ID de pago no presente.");
            }

            long idPago = Long.parseLong(idPagoObj.toString());

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(idPago);

            // Extraer ID de la orden desde metadata
            Object idOrdenObj = payment.getMetadata().get("id_orden");
            if (idOrdenObj == null) {
                throw new IllegalArgumentException("El pago no tiene ID de orden en los metadatos.");
            }

            String idOrden = idOrdenObj.toString();

            // Guardar el pago en la orden
            Orden orden = obtenerOrdenPorId(idOrden);
            Pago pago = crearPago(payment);
            orden.setPago(pago);
            ordenRepository.save(orden);

        } catch (Exception e) {
            log.error("Error al procesar notificación de MercadoPago: {}", e.getMessage(), e);
        }
    }

    private Pago crearPago(Payment payment) {
        Pago pago = new Pago();
        pago.setId(payment.getId().toString());
        pago.setFecha( payment.getDateCreated().toLocalDateTime() );
        pago.setEstado(payment.getStatus());
        pago.setDetalleEstado(payment.getStatusDetail());
        pago.setTipoPago(payment.getPaymentTypeId());
        pago.setMoneda(payment.getCurrencyId());
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());
        pago.setValorTransaccion(payment.getTransactionAmount().floatValue());
        return pago;
    }
}