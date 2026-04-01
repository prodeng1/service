package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.MasinaRepository;
import ro.unibuc.prodeng.repository.SaleRepository;
import ro.unibuc.prodeng.request.MasinaRequest;
import ro.unibuc.prodeng.request.SaleRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Masina and Sale Integration Tests")
class MasinaAndSaleIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MasinaRepository masinaRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        saleRepository.deleteAll();
        masinaRepository.deleteAll();
    }

    private String createMasina(String marca, String model, int an, double pret) throws Exception {
        MasinaRequest request = new MasinaRequest(
            marca, model, an, pret, 0, "Benzina", 100, "test@example.com"
        );

        String response = mockMvc.perform(post("/api/masini")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private String createSale(String masinaId, String client, double pret) throws Exception {
        SaleRequest request = new SaleRequest(masinaId, client, pret);

        String response = mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void testCreateSaleAndPreventMasinaDeletion() throws Exception {
        // Arrange
        String masinaId = createMasina("Dacia", "Logan", 2022, 10000);
        createSale(masinaId, "Ion Popescu", 9500);

        // Act & Assert - Delete should fail
        mockMvc.perform(delete("/api/masini/" + masinaId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Masina a fost vanduta si nu mai poate fi stearsa"));
        
        // Act & Assert - Update should fail
        MasinaRequest updateReq = new MasinaRequest(
            "Dacia", "Sandero", 2022, 11000, 10, "Benzina", 100, "test@example.com"
        );
        
        mockMvc.perform(put("/api/masini/" + masinaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Masina a fost vanduta si nu mai poate fi modificata"));
    }
}
