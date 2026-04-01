package ro.unibuc.prodeng.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.prodeng.model.Sale;
import ro.unibuc.prodeng.repository.MasinaRepository;
import ro.unibuc.prodeng.repository.SaleRepository;
import ro.unibuc.prodeng.request.SaleRequest;
import ro.unibuc.prodeng.response.SaleResponse;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private MasinaRepository masinaRepository;

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SaleResponse createSale(SaleRequest request) {
        if (!masinaRepository.existsById(request.getMasinaId())) {
            throw new IllegalArgumentException("Masina cu ID-ul specificat nu exista");
        }
        if (saleRepository.existsByMasinaId(request.getMasinaId())) {
            throw new IllegalArgumentException("Aceasta masina a fost deja vanduta");
        }

        Sale sale = new Sale(
                null,
                request.getMasinaId(),
                request.getNumeClient(),
                request.getPretFinal()
        );
        Sale saved = saleRepository.save(sale);
        return toResponse(saved);
    }

    private SaleResponse toResponse(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getMasinaId(),
                sale.getNumeClient(),
                sale.getPretFinal()
        );
    }
}
