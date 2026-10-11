package com.bank.customerservice.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;


interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {

}
