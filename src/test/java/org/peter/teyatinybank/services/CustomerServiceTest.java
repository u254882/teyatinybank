package org.peter.teyatinybank.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.peter.teyatinybank.domain.Account;
import org.peter.teyatinybank.domain.Customer;
import org.peter.teyatinybank.repositories.AccountRepository;
import org.peter.teyatinybank.repositories.CustomerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomerWithAccountSuccess() {
        Customer customer = new Customer();
        Account account = new Account();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = customerService.createCustomerWithAccount(customer);

        assertNotNull(result);
        assertEquals(account, result);
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createCustomerWithAccountFailure() {
        assertThrows(NullPointerException.class, () -> customerService.createCustomerWithAccount(null));
    }
    @Test
    void deactivateCustomerSuccess() {
        Customer customer = new Customer();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        doNothing().when(customerRepository).deactivateCustomer(anyLong());

        assertDoesNotThrow(() -> customerService.deactivateCustomer(1L));
        verify(customerRepository, times(1)).deactivateCustomer(anyLong());
    }
    @Test

    void deactivateCustomerFailureWhenCustomerDoesNotExist() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> customerService.deactivateCustomer(1L));
        verify(customerRepository, times(0)).deactivateCustomer(anyLong());
    }
    @Test
    void deactivateCustomerFailure() {
        assertThrows(IllegalArgumentException.class, () -> customerService.deactivateCustomer(null));
    }
}