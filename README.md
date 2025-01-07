# Core Banking Transaction System
A Spring Boot-based backend application designed to handle core banking functionalities, including account management, complex fee management with proper distributions, transactions, balance inquiries and more.
This project focuses on providing a secure and efficient API-driven system for core banking operations. Being a core engine, efficient transaction occuring while facilitating complex charge policies is 
prioritized here. No frontend is focused in this project.

### Features

- Account Management: Create, update, and delete customers.
- Multiple account creation under the same customer.
- Creation of multiple types of transaction.
- Complex charging policies applied based on transaction type.
- Separate multiple account maintenance for all targeted recipients of charges.
- Realtime distrubution of charge fees among all taregeted accounts as a transaction occurs.
- Transaction Handling: Perform deposits and withdrawals. 
- Balance Inquiry: Check account balances in realtime.
- Secure APIs: As this application won't be directly accessed by customers or business users, basic authentication is applied.

### Tech Stack
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA (Hibernate)
- MySQL (Database)
- REST APIs

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Tanjemul/core-banking-system.git

2. Navigate to the project directory: cd \CBS

3. Configure the database:
Update application.yaml with your database credentials. This project uses mysql database.
4. Build and run the project:
    ```bash
   ./mvn clean
   ./mvn dependency:tree
   ./mvn spring-boot:run

## API Documentation

The complete API documentation is available [here](https://documenter.getpostman.com/view/25076611/2sAYJ6CfgX).

You can explore all endpoints, parameters, and responses interactively.

     
