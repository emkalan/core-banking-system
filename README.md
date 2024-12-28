# Core Banking Transaction System
A Spring Boot-based backend application designed to handle core banking functionalities, including account management, complex fee management with proper distributions, transactions, balance inquiries and others.
This project focuses on providing a secure and efficient API-driven system for core banking operations. Being a core engine, efficient transaction occuring while facilitating complex charge policies is 
prioratized here. No forntend is focused in the project.

### Features

- Account Management: Create, update, and delete customers.
- Multiple account creation under the same customer.
- Multiple types of transaction type creation.
- Based on transaction types, complex charging policies can be applied.
- Separate multiple account maintaining for all the targeted recipients of the charges.
- Realtime distrubution of charge fees among all the taregeted accounts at the same time the transaction occurs.
- Transaction Handling: Perform deposits, withdrawals. 
- Balance Inquiry: Check account balances in real-time.
- Secure APIs: As this application won't be directly accessed to the customers or other business users, basic authentication is applied.

### Tech Stack
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA (Hibernate)
- MySQL (Database)
- REST APIs
