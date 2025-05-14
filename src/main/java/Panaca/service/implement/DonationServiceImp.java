package Panaca.service.implement;

import Panaca.dto.donation.DonationDTO;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.Donation;
import Panaca.model.documents.DonationItem;
import Panaca.repository.CuentaRepository;
import Panaca.repository.DonationRepository;
import Panaca.service.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Validated

public class DonationServiceImp implements DonationService {

    private final DonationRepository donationRepo;
    private final CuentaRepository cuentaRepo;


    public void crearDonacion(DonationDTO donationDTO) {
        Cuenta donante = cuentaRepo.findById(donationDTO.donanteId())
                .orElseThrow(() -> new RuntimeException("La cuenta del donante no existe"));

        Donation donation = new Donation();
        donation.setIdDonante(donante.getId());
        donation.setFecha(LocalDateTime.now());

        List<DonationItem> items = donationDTO.items().stream().map(itemDTO -> {
            int unitPrice = itemDTO.tipoAnimal().getPricePerBulto();
            int subtotal = unitPrice * itemDTO.cantidadBultos();
            return new DonationItem(
                    null,
                    null, // se setea después
                    itemDTO.tipoAnimal(),
                    itemDTO.cantidadBultos(),
                    unitPrice,
                    subtotal
            );
        }).toList();

        // asignamos relación hacia atrás
        items.forEach(item -> item.setDonationId(donation.getId()));

        donation.setItems(items);
        donation.setTotal(items.stream().mapToInt(DonationItem::getSubtotal).sum());

        donationRepo.save(donation);
    }
}