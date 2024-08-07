package org.peter.teyatinybank.services;

import org.peter.teyatinybank.domain.Account;
import org.peter.teyatinybank.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.peter.teyatinybank.repositories.AccountRepository;
import org.peter.teyatinybank.repositories.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createCustomerWithAccount(Customer customer) {
        customer.setIsActive(true);
        Customer savedCustomer = customerRepository.save(customer);
        Account account = new Account();
        account.setCustomer(savedCustomer);
        return accountRepository.save(account);
    }


    public void deactivateCustomer(Long id) {
       customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customerRepository.deactivateCustomer(id);
    }
}