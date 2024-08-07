package org.peter.teyatinybank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.peter.teyatinybank.domain.TransactionEvent;
import org.peter.teyatinybank.repositories.AccountRepository;
import org.peter.teyatinybank.repositories.TransactionEventRepository;
import org.peter.teyatinybank.request.TransactionHistoryRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class TransactionEventServiceTest {

    @Mock
    TransactionEventRepository transactionEventRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    TransactionEventService transactionEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactionHistoryOfAccountSuccess() {
        TransactionHistoryRequest request = new TransactionHistoryRequest();
        request.setAccountId(1L);
        TransactionEvent event = new TransactionEvent();
        when(transactionEventRepository.findAllByMainAccountOrSecondAccount(anyLong(), anyLong())).thenReturn(Collections.singletonList(event));

        List<TransactionEvent> result = transactionEventService.getTransactionHistoryOfAccount(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
    }

    @Test
    void getTransactionHistoryOfAccountNoTransactions() {
        TransactionHistoryRequest request = new TransactionHistoryRequest();
        request.setAccountId(1L);
        when(transactionEventRepository.findAllByMainAccountOrSecondAccount(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        List<TransactionEvent> result = transactionEventService.getTransactionHistoryOfAccount(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}