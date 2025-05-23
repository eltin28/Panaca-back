package Panaca.service.service;

import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.model.enums.EstadoDevolucion;

import java.util.List;

public interface DevolucionService {

    DevolucionResponseDTO solicitar(DevolucionRequestDTO dto);

    List<DevolucionResponseDTO> listarPorUsuario(String cuentaId);

    List<DevolucionResponseDTO> listarTodas();

    DevolucionResponseDTO cambiarEstado(String id, EstadoDevolucion nuevoEstado);

}