package Panaca.dto.donation;

import java.util.List;

public class DonationDTO {
    private String donanteId;
    private List<DonationItemDTO> items;
    
    // Getters y Setters
    public String getDonanteId() {
        return donanteId;
    }

    public void setDonanteId(String donanteId) {
        this.donanteId = donanteId;
    }

    public List<DonationItemDTO> getItems() {
        return items;
    }

    public void setItems(List<DonationItemDTO> items) {
        this.items = items;
    }
}
