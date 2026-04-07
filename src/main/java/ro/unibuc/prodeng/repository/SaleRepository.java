package ro.unibuc.prodeng.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ro.unibuc.prodeng.model.Sale;

@Repository
public interface SaleRepository extends MongoRepository<Sale, String> {
    boolean existsByMasinaId(String masinaId);
}
