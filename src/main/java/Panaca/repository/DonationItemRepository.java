package Panaca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Panaca.model.donation.DonationItem;

public interface DonationItemRepository extends JpaRepository<DonationItem, Long> { }
