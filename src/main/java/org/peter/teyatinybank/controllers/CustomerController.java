package org.peter.teyatinybank.controllers;

import org.peter.teyatinybank.domain.Account;
import org.peter.teyatinybank.domain.Customer;
import jakarta.validation.Valid;
import org.peter.teyatinybank.response.CreationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.peter.teyatinybank.services.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CreationResponse> createCustomer(@RequestBody @Valid Customer customer) {
        try {
            logger.info("Attempting to create a customer");
            Account createdAccount = customerService.createCustomerWithAccount(customer);
            logger.info("Customer created successfully with ID: " + createdAccount.getCustomer().getId());
            return new ResponseEntity<>(new CreationResponse(createdAccount.getCustomer().getId(),createdAccount.getId()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to create customer", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@RequestBody @Valid Long customerId) {
        try {
            logger.info("Attempting to deactivate customer with ID: " + customerId);
            customerService.deactivateCustomer(customerId);
            logger.info("Customer deactivated successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to deactivate customer", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}