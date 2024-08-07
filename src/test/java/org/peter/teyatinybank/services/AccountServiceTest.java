package org.peter.teyatinybank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.peter.teyatinybank.domain.Account;
import org.peter.teyatinybank.domain.Customer;
import org.peter.teyatinybank.repositories.AccountRepository;
import org.peter.teyatinybank.repositories.TransactionEventRepository;
import org.peter.teyatinybank.request.DepositRequest;
import org.peter.teyatinybank.request.TransferRequest;
import org.peter.teyatinybank.request.WithdrawRequest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionEventRepository transactionEventRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldDepositAmountWhenAccountExistsAndCustomerIsActive() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Peter");
        customer.setLastName("Szilagyi");
        customer.setAddress("1 Main St.");
        customer.setIsActive(true);
        Account account = new Account(1L, BigDecimal.valueOf(100.0), customer);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(50.0));
        accountService.deposit(depositRequest);
        assertEquals(BigDecimal.valueOf(150.0), account.getCurrentBalance());
    }
    @Test
    void shouldThrowExceptionWhenDepositAmountWhenAccountExistsAndCustomerIsNotActive() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Peter");
        customer.setLastName("Szilagyi");
        customer.setAddress("1 Main St.");
        customer.setIsActive(false);
        Account account = new Account(1L, BigDecimal.valueOf(100.0), customer);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(50.0));

        assertThrows(RuntimeException.class, () -> accountService.deposit(depositRequest));

    }
    @Test
    void shouldThrowExceptionWhenAccountDoesNotExistForDeposit() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(50.0));

        assertThrows(RuntimeException.class, () -> accountService.deposit(depositRequest));
    }

    @Test
    void shouldWithdrawAmountWhenAccountExistsCustomerIsActiveAndBalanceIsSufficient() {
        Account account = new Account(1L, BigDecimal.valueOf(100.0), new Customer());
        account.getCustomer().setIsActive(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(1L);
        withdrawRequest.setAmount(BigDecimal.valueOf(50.0));

        accountService.withdraw(withdrawRequest);

        assertEquals(BigDecimal.valueOf(50.0), account.getCurrentBalance());
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExistForWithdrawal() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(1L);
        withdrawRequest.setAmount(BigDecimal.valueOf(50.0));

        assertThrows(IllegalArgumentException.class, () -> accountService.withdraw(withdrawRequest));
    }

    @Test
    void shouldTransferAmountWhenBothAccountsExistCustomersAreActiveAndSourceAccountBalanceIsSufficient() {
        Customer customerFrom = new Customer();
        customerFrom.setId(1L);
        customerFrom.setFirstName("Peter");
        customerFrom.setLastName("Szilagyi");
        customerFrom.setAddress("1 Main St.");
        Customer customerTo = new Customer();
        customerFrom.setId(2L);
        customerFrom.setFirstName("Paul");
        customerFrom.setLastName("From");
        customerFrom.setAddress("1 Other St.");

        Account fromAccount = new Account(1L, BigDecimal.valueOf(100.0), customerFrom);
        fromAccount.getCustomer().setIsActive(true);
        Account toAccount = new Account(2L, BigDecimal.valueOf(100.0),customerTo);
        toAccount.getCustomer().setIsActive(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(1L);
        transferRequest.setToAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(50.0));

        accountService.transfer(transferRequest);

        assertEquals(BigDecimal.valueOf(50.0), fromAccount.getCurrentBalance());
        assertEquals(BigDecimal.valueOf(150.0), toAccount.getCurrentBalance());
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountDoesNotExistForTransfer() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(1L);
        transferRequest.setToAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(50.0));

        assertThrows(RuntimeException.class, () -> accountService.transfer(transferRequest));
    }
    @Test
    void shouldThrowExceptionWhenSourceAccountInactive() {
        Customer customerFrom = new Customer();
        customerFrom.setId(1L);
        customerFrom.setFirstName("Peter");
        customerFrom.setLastName("Szilagyi");
        customerFrom.setAddress("1 Main St.");
        Customer customerTo = new Customer();
        customerFrom.setId(2L);
        customerFrom.setFirstName("Paul");
        customerFrom.setLastName("From");
        customerFrom.setAddress("1 Other St.");

        Account fromAccount = new Account(1L, BigDecimal.valueOf(100.0), customerFrom);
        fromAccount.getCustomer().setIsActive(false);
        Account toAccount = new Account(2L, BigDecimal.valueOf(100.0),customerTo);
        toAccount.getCustomer().setIsActive(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(1L);
        transferRequest.setToAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(50.0));
        assertThrows(RuntimeException.class, () -> accountService.transfer(transferRequest));
    }
    @Test
    void shouldThrowExceptionWhenTargetAccountInactive() {
        Customer customerFrom = new Customer();
        customerFrom.setId(1L);
        customerFrom.setFirstName("Peter");
        customerFrom.setLastName("Szilagyi");
        customerFrom.setAddress("1 Main St.");
        Customer customerTo = new Customer();
        customerFrom.setId(2L);
        customerFrom.setFirstName("Paul");
        customerFrom.setLastName("From");
        customerFrom.setAddress("1 Other St.");

        Account fromAccount = new Account(1L, BigDecimal.valueOf(100.0), customerFrom);
        fromAccount.getCustomer().setIsActive(false);
        Account toAccount = new Account(2L, BigDecimal.valueOf(100.0),customerTo);
        toAccount.getCustomer().setIsActive(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(1L);
        transferRequest.setToAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(50.0));
        assertThrows(RuntimeException.class, () -> accountService.transfer(transferRequest));
    }
}