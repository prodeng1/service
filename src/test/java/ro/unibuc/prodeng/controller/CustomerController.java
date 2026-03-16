package ro.unibuc.prodeng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.request.CreateCustomerRequest;
import ro.unibuc.prodeng.request.UpdateCustomerRequest;
import ro.unibuc.prodeng.response.CustomerResponse;
import ro.unibuc.prodeng.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Task 2 – lista clienti
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Task 1 – creare client
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @RequestBody CreateCustomerRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    // Task 3 – modificare date
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerRequest request)
            throws EntityNotFoundException {

        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    // Task 4 – stergere client
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id)
            throws EntityNotFoundException {

        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}