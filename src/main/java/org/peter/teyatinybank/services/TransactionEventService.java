package org.peter.teyatinybank.services;

import org.peter.teyatinybank.domain.TransactionEvent;
import org.peter.teyatinybank.repositories.AccountRepository;
import org.peter.teyatinybank.repositories.TransactionEventRepository;
import org.peter.teyatinybank.request.TransactionHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionEventService {

    private final TransactionEventRepository transactionEventRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionEventService(TransactionEventRepository transactionEventRepository, AccountRepository accountRepository) {
        this.transactionEventRepository = transactionEventRepository;
        this.accountRepository = accountRepository;
    }

    public List<TransactionEvent> getTransactionHistoryOfAccount(TransactionHistoryRequest transactionHistoryRequest) {
        return transactionEventRepository.findAllByMainAccountOrSecondAccount(transactionHistoryRequest.getAccountId(), transactionHistoryRequest.getAccountId());
    }

}