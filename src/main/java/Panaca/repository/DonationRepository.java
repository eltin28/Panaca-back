package Panaca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Panaca.model.donation.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> { }
