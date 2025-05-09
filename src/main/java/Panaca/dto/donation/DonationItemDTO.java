package Panaca.dto.donation;

import Panaca.model.donation.AnimalType;

public class DonationItemDTO {
    private AnimalType tipoAnimal;
    private int cantidadBultos;
    
    // Getters y Setters
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
}
