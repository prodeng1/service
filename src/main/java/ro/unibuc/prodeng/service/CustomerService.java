package ro.unibuc.prodeng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.CustomerEntity;
import ro.unibuc.prodeng.repository.CustomerRepository;
import ro.unibuc.prodeng.request.CreateCustomerRequest;
import ro.unibuc.prodeng.request.UpdateCustomerRequest;
import ro.unibuc.prodeng.response.CustomerResponse;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        CustomerEntity customer = new CustomerEntity(
                null,
                request.name(),
                request.email(),
                request.phone()
        );

        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(String id, UpdateCustomerRequest request)
            throws EntityNotFoundException {

        CustomerEntity existing = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        CustomerEntity updated = new CustomerEntity(
                existing.id(),
                request.name(),
                request.email(),
                request.phone()
        );

        return toResponse(customerRepository.save(updated));
    }

    public void deleteCustomer(String id) throws EntityNotFoundException {

        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }

        // aici s-ar verifica daca exista achizitii active
        // pentru proiect basic presupunem ca nu are

        customerRepository.deleteById(id);
    }

    private CustomerResponse toResponse(CustomerEntity c) {
        return new CustomerResponse(
                c.id(),
                c.name(),
                c.email(),
                c.phone()
        );
    }
}