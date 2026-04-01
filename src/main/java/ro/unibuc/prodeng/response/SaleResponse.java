package ro.unibuc.prodeng.response;

public class SaleResponse {

    private String id;
    private String masinaId;
    private String numeClient;
    private double pretFinal;

    public SaleResponse() {}

    public SaleResponse(String id, String masinaId, String numeClient, double pretFinal) {
        this.id = id;
        this.masinaId = masinaId;
        this.numeClient = numeClient;
        this.pretFinal = pretFinal;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMasinaId() { return masinaId; }
    public void setMasinaId(String masinaId) { this.masinaId = masinaId; }

    public String getNumeClient() { return numeClient; }
    public void setNumeClient(String numeClient) { this.numeClient = numeClient; }

    public double getPretFinal() { return pretFinal; }
    public void setPretFinal(double pretFinal) { this.pretFinal = pretFinal; }
}
