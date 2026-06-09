# Bank Account System

A console-based banking system built with Java and Maven, focusing on clean architecture, business rules, transaction validation, and audit-friendly transaction logging.

## Overview

This project simulates a simple banking system where users can create accounts, deposit money, withdraw money, transfer funds, and view recent transactions.

The main goal of the project is not only to build a working console application, but also to demonstrate clean separation of responsibilities between the user interface, business logic, validation policies, and transaction logging.

## Features

- Create bank accounts with unique generated IDs
- Deposit money into accounts
- Withdraw money from accounts
- Transfer money between accounts
- Reject invalid transactions with clear rejection reasons
- Prevent negative or invalid amounts
- Prevent accounts from being created with invalid data
- Enforce maximum transaction amount
- Enforce daily outgoing transaction limit
- Keep a transaction log for successful and rejected operations
- Show recent transactions for each account
- Unit-tested business rules with JUnit 5

## Tech Stack

- Java 17
- Maven
- JUnit 5
- IntelliJ IDEA

## Project Structure

```text
src
├── main
│   └── java
│       └── bank
│           ├── Main.java
│           ├── core
│           │   ├── Account.java
│           │   ├── Bank.java
│           │   └── Transaction.java
│           ├── ledger
│           │   └── TransactionLog.java
│           ├── policy
│           │   └── TransactionPolicy.java
│           └── state
│               └── AccountState.java
└── test
    └── java
        └── bank
            └── core
                └── BankTest.java