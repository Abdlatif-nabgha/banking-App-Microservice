package com.bank.customerservice.adapter.in.web;

import com.bank.customerservice.application.port.in.CustomerUseCase;
import com.bank.customerservice.domain.Customer;
import com.bank.customerservice.domain.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
class CustomerController {

    record CustomerRequest(String firstName, String lastName, String email, String phoneNumber) {
        CustomerUseCase.CustomerDetails toDetails() { return new CustomerUseCase.CustomerDetails(firstName, lastName, email, phoneNumber); }
    }
    record CustomerResponse(Long id, String firstName, String lastName, String email, String phoneNumber) {
        static CustomerResponse from(Customer c) {
            return new CustomerResponse(c.id(), c.firstName(), c.lastName(), c.email(), c.phoneNumber());
        }
    }

    private final CustomerUseCase useCase;

    @GetMapping
    List<CustomerResponse> all() { return useCase.findAll().stream().map(CustomerResponse::from).toList(); }

    @GetMapping("/{id}")
    CustomerResponse one(@PathVariable Long id) { return CustomerResponse.from(useCase.findById(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CustomerResponse create(@RequestBody CustomerRequest r) { return CustomerResponse.from(useCase.create(r.toDetails())); }

    @PutMapping("/{id}")
    CustomerResponse update(@PathVariable Long id, @RequestBody CustomerRequest r) {
        return CustomerResponse.from(useCase.update(id, r.toDetails()));
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) { useCase.delete(id); }

    @ExceptionHandler(CustomerNotFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFound(CustomerNotFoundException e) { return e.getMessage(); }

}
