package Panaca.model.donation;

import Panaca.model.documents.Cuenta;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Donation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Cuenta donante;

    private LocalDateTime fecha;

    private int total;             

    @OneToMany(mappedBy="donation", cascade=CascadeType.ALL)
    private List<DonationItem> items;

     // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cuenta getDonante() {
        return donante;
    }

    public void setDonante(Cuenta donante) {
        this.donante = donante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DonationItem> getItems() {
        return items;
    }

    public void setItems(List<DonationItem> items) {
        this.items = items;
    }
}
