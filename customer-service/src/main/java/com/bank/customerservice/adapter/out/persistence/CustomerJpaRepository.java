package com.bank.customerservice.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;


interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {

}
