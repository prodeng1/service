package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class SaleRequest {

    @NotBlank(message = "ID-ul masinii este obligatoriu")
    private String masinaId;

    @NotBlank(message = "Numele clientului este obligatoriu")
    private String numeClient;

    @Positive(message = "Pretul final trebuie sa fie pozitiv")
    private double pretFinal;

    public SaleRequest() {}

    public SaleRequest(String masinaId, String numeClient, double pretFinal) {
        this.masinaId = masinaId;
        this.numeClient = numeClient;
        this.pretFinal = pretFinal;
    }

    public String getMasinaId() { return masinaId; }
    public void setMasinaId(String masinaId) { this.masinaId = masinaId; }

    public String getNumeClient() { return numeClient; }
    public void setNumeClient(String numeClient) { this.numeClient = numeClient; }

    public double getPretFinal() { return pretFinal; }
    public void setPretFinal(double pretFinal) { this.pretFinal = pretFinal; }
}
