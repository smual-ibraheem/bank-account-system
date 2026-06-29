# 🏦 Bank Account System

> **⚠️ ARCHITECTURE EVOLUTION NOTICE: This is V1 (In-Memory Version)**
>
> This repository demonstrates the foundational business logic, guard clauses, and domain rules using in-memory collections. The system has since been **fully upgraded to an Enterprise-level JDBC architecture** featuring persistent SQL storage, ACID transactions, and Optimistic Locking.
>
> 👉 **[Click here to view V2: Enterprise Banking Core (JDBC)](https://github.com/smual-ibraheem/enterprise-banking-core-jdbc)**

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Maven](https://img.shields.io/badge/Maven-3.3-C71A36.svg)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162.svg)

*A console-based banking system built with Java and Maven, focusing on clean architecture, business rules, transaction validation, and audit-friendly transaction logging.*

---

## 📌 Overview

This project simulates a simple banking system where users can create accounts, deposit money, withdraw money, transfer funds, and view recent transactions.

The main goal of the project is not only to build a working console application, but also to demonstrate **clean separation of responsibilities** between the user interface, business logic, validation policies, and transaction logging.

---

## 🚀 Features

- ✔️ **Create bank accounts** with unique generated IDs
- ✔️ **Deposit money** into accounts
- ✔️ **Withdraw money** from accounts
- ✔️ **Transfer money** between accounts
- ✔️ **Reject invalid transactions** with clear rejection reasons
- ✔️ **Prevent negative** or invalid amounts
- ✔️ **Prevent accounts** from being created with invalid data
- ✔️ **Enforce** maximum transaction amount
- ✔️ **Enforce** daily outgoing transaction limit
- ✔️ **Keep a transaction log** for successful and rejected operations
- ✔️ **Show recent transactions** for each account
- ✔️ **Unit-tested business rules** with JUnit 5

---

## ⚙️ Tech Stack

* **Java 17**
* **Maven**
* **JUnit 5**
* **IntelliJ IDEA**

---

## 🏗️ Project Structure

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