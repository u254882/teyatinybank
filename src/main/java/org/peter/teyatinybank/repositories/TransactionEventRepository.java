package org.peter.teyatinybank.repositories;

import org.peter.teyatinybank.domain.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, Long> {
    List<TransactionEvent> findAllByMainAccountOrSecondAccount(Long mainAccoint, Long secondAccount);

}
