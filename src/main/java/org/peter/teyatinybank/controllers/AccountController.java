package org.peter.teyatinybank.controllers;

import jakarta.validation.Valid;
import org.peter.teyatinybank.domain.TransactionEvent;
import org.peter.teyatinybank.request.*;
import org.peter.teyatinybank.services.AccountService;
import org.peter.teyatinybank.services.TransactionEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final TransactionEventService transactionEventService;

    @Autowired
    public AccountController(AccountService accountService, TransactionEventService transactionEventService) {
        this.accountService = accountService;
        this.transactionEventService = transactionEventService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody @Valid DepositRequest depositRequest) {
        try {
            logger.info("Attempting to deposit to account");
            accountService.deposit(depositRequest);
            logger.info("Deposit successful");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Deposit failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody @Valid WithdrawRequest withdrawRequest) {
        try {
            logger.info("Attempting to withdraw from account");
            accountService.withdraw(withdrawRequest);
            logger.info("Withdrawal successful");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Withdrawal failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest transferRequest) {
        try {
            logger.info("Attempting to transfer between accounts");
            accountService.transfer(transferRequest);
            logger.info("Transfer successful");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Transfer failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestBody @Valid BalanceRequest balanceRequest) {
        try {
            logger.info("Fetching balance for account ID: " + balanceRequest.getAccountId());
            BigDecimal balance = accountService.getBalance(balanceRequest);
            logger.info("Balance fetched successfully");
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to fetch balance", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> getTransactionHistory(@RequestBody @Valid TransactionHistoryRequest transactionHistoryRequest) {
        try {
            logger.info("Fetching transaction history for account ID: " + transactionHistoryRequest.getAccountId());
            List<TransactionEvent> transactionEvents = transactionEventService.getTransactionHistoryOfAccount(transactionHistoryRequest);
            logger.info("Transaction history fetched successfully");
            return new ResponseEntity<>(transactionEvents, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to fetch transaction history", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("/{accountId}/balance")
//    public ResponseEntity<?> getBalance(@PathVariable Long accountId) {
//        try {
//            logger.info("Fetching balance for account ID: " + accountId);
//            BigDecimal balance = accountService.getBalance(accountId);
//            logger.info("Balance fetched successfully");
//            return new ResponseEntity<>(balance, HttpStatus.OK);
//        } catch (IllegalArgumentException e) {
//            logger.error("Failed to fetch balance", e);
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/{accountId}/transactions")
//    public ResponseEntity<?> getTransactionHistory(@PathVariable Long accountId) {
//        try {
//            logger.info("Fetching transaction history for account ID: " + accountId);
//            List<TransactionEvent> transactionEvents = transactionEventService.getTransactionHistoryPerAccount(accountId);
//            logger.info("Transaction history fetched successfully");
//            return new ResponseEntity<>(transactionEvents, HttpStatus.OK);
//        } catch (IllegalArgumentException e) {
//            logger.error("Failed to fetch transaction history", e);
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
}