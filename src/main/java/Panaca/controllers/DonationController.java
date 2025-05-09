package Panaca.controllers;

import Panaca.dto.donation.*;
import Panaca.model.donation.Donation;
import Panaca.service.service.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public ResponseEntity<Donation> donate(@RequestBody DonationDTO dto) {
        var saved = donationService.crearDonacion(dto);
        return ResponseEntity.ok(saved);
    }
}
