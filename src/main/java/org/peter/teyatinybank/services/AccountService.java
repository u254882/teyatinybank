package org.peter.teyatinybank.services;

import org.peter.teyatinybank.domain.Account;
import org.peter.teyatinybank.domain.TransactionEvent;
import org.peter.teyatinybank.domain.TransactionType;
import org.peter.teyatinybank.repositories.AccountRepository;
import org.peter.teyatinybank.repositories.TransactionEventRepository;
import org.peter.teyatinybank.request.BalanceRequest;
import org.peter.teyatinybank.request.DepositRequest;
import org.peter.teyatinybank.request.TransferRequest;
import org.peter.teyatinybank.request.WithdrawRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionEventRepository transactionEventRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransactionEventRepository transactionEventRepository) {
        this.accountRepository = accountRepository;
        this.transactionEventRepository = transactionEventRepository;
    }

    @Transactional
    public void deposit(DepositRequest depositRequest) {

        try {
            Account account = accountRepository.findById(depositRequest.getAccountId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));
            TransactionEvent transactionEvent = TransactionEvent.builder()
                    .date(LocalDateTime.now())
                    .transactionType(TransactionType.DEPOSIT)
                    .mainAccount(account.getId())
                    .amount(depositRequest.getAmount())
                    .build();
            if (!account.getCustomer().getIsActive()) {
                throw new IllegalArgumentException("Inactive customer cannot deposit money");
            }
            account.setCurrentBalance(account.getCurrentBalance().add(transactionEvent.getAmount()));
            accountRepository.save(account);
            transactionEventRepository.save(transactionEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void withdraw(WithdrawRequest withdrawRequest) {
        Account account = accountRepository.findById(withdrawRequest.getAccountId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        TransactionEvent transactionEvent = TransactionEvent.builder()
                .date(LocalDateTime.now())
                .transactionType(TransactionType.WITHDRAWAL)
                .mainAccount(account.getId())
                .amount(withdrawRequest.getAmount())
                .build();
        if (!account.getCustomer().getIsActive()) {
            throw new IllegalArgumentException("Inactive customer cannot withdraw money");
        }
        if (account.getCurrentBalance().compareTo(transactionEvent.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        account.setCurrentBalance(account.getCurrentBalance().subtract(transactionEvent.getAmount()));
        accountRepository.save(account);
        transactionEventRepository.save(transactionEvent);
    }

    @Transactional
    public void transfer(TransferRequest transferRequest) {

         CompletableFuture.supplyAsync(() ->
                accountRepository.findById(transferRequest.getFromAccountId()).orElseThrow(() -> new IllegalArgumentException("Source account not found"))
        ).thenApply(fromAccount -> {
            if (!fromAccount.getCustomer().getIsActive()) {
                throw new IllegalArgumentException("Inactive customer cannot transfer money");
            }
            if (fromAccount.getCurrentBalance().compareTo(transferRequest.getAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient balance in source account");
            }
            return fromAccount;
        }).thenCombine(
                CompletableFuture.supplyAsync(() ->
                        accountRepository.findById(transferRequest.getToAccountId()).orElseThrow(() -> new IllegalArgumentException("Destination account not found"))
                ).thenApply(toAccount -> {
                    if (!toAccount.getCustomer().getIsActive()) {
                        throw new IllegalArgumentException("Inactive customer cannot receive money");
                    }
                    return toAccount;
                }), (from, to) -> {
                    from.setCurrentBalance(from.getCurrentBalance().subtract(transferRequest.getAmount()));
                    to.setCurrentBalance(to.getCurrentBalance().add(transferRequest.getAmount()));
                    accountRepository.save(from);
                    accountRepository.save(to);
                    TransactionEvent transactionEvent = TransactionEvent.builder()
                            .date(LocalDateTime.now())
                            .transactionType(TransactionType.WITHDRAWAL)
                            .mainAccount(from.getId())
                            .secondAccount(to.getId())
                            .amount(transferRequest.getAmount())
                            .transactionType(TransactionType.TRANSFER)
                            .build();
                    transactionEventRepository.save(transactionEvent);
                    return to;
                }).join();
    }

    public BigDecimal getBalance(BalanceRequest balanceRequest) {
        Account account = accountRepository.findById(balanceRequest.getAccountId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!account.getCustomer().getIsActive()) {
            throw new IllegalArgumentException("Inactive customer cannot view their balance");
        }
        return account.getCurrentBalance();
    }
}