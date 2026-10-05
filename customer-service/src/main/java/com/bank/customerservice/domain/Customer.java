package com.bank.customerservice.domain;


import java.util.Locale;

public final class Customer {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;

    public Customer(Long id, String firstName, String lastName, String email, String phoneNumber) {
        this.id = id;
        this.firstName = requireText(firstName, "First name");
        this.lastName = requireText(lastName, "Last name");
        this.email = normalizeEmail(email);
        this.phoneNumber = phoneNumber == null || phoneNumber.isBlank()
                    ? null
                    : phoneNumber.strip();
    }

    public Customer withDetails(String firstName, String lastName,
                                String email, String phoneNumber) {
        return new Customer(id, firstName, lastName, email, phoneNumber);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.strip();
    }

    private static String normalizeEmail(String email) {
        return requireText(email, "Email").toLowerCase(Locale.ROOT);
    }

    public Long id() {
        return id;
    }
    public String firstName() {
        return firstName;
    }
    public String lastName() {
        return lastName;
    }
    public String email() {
        return email;
    }
    public String phoneNumber() {
        return phoneNumber;
    }
}
