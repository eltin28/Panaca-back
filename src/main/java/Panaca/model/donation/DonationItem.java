package Panaca.model.donation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


@Entity
public class DonationItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Donation donation;

    @Enumerated(EnumType.STRING)
    private AnimalType tipoAnimal;

    private int cantidadBultos;
    private int precioUnitario;      // se copia desde AnimalType
    private int subtotal;            // cantidadBultos * precioUnitario

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public AnimalType getTipoAnimal() {
        return tipoAnimal;
    }

    public void setTipoAnimal(AnimalType tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
    }

    public int getCantidadBultos() {
        return cantidadBultos;
    }

    public void setCantidadBultos(int cantidadBultos) {
        this.cantidadBultos = cantidadBultos;
    }

    public int getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(int precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }
}
