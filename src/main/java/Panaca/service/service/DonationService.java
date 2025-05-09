package Panaca.service.service;

import Panaca.dto.donation.*;
import Panaca.model.donation.*;
import Panaca.repository.DonationRepository;
import Panaca.repository.DonationItemRepository;
import Panaca.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DonationService {
    private final DonationRepository donationRepo;
    private final CuentaRepository cuentaRepo;

    public DonationService(DonationRepository donationRepo, CuentaRepository cuentaRepo) {
        this.donationRepo = donationRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Transactional
    public Donation crearDonacion(DonationDTO dto) {
        var donante = cuentaRepo.findById(dto.getDonanteId())
                        .orElseThrow(() -> new RuntimeException("Cuenta no existe"));
        var donation = new Donation();
        donation.setDonante(donante);
        donation.setFecha(LocalDateTime.now());

        var items = dto.getItems().stream().map(itemDto -> {
            var tipo = itemDto.getTipoAnimal();
            var unit = tipo.getPricePerBulto();
            var di = new DonationItem();
            di.setDonation(donation);
            di.setTipoAnimal(tipo);
            di.setCantidadBultos(itemDto.getCantidadBultos());
            di.setPrecioUnitario(unit);
            di.setSubtotal(unit * itemDto.getCantidadBultos());
            return di;
        }).toList();

        donation.setItems(items);
        donation.setTotal(items.stream().mapToInt(DonationItem::getSubtotal).sum());
        return donationRepo.save(donation);
    }
}
