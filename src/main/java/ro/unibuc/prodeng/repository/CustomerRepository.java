package ro.unibuc.prodeng.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.CustomerEntity;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findByEmail(String email);

}