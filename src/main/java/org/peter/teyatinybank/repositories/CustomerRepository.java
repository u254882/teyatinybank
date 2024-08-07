package org.peter.teyatinybank.repositories;


import org.peter.teyatinybank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Customer u SET u.isActive = false WHERE u.id = :id")
    void deactivateCustomer(@Param("id") Long id);
}