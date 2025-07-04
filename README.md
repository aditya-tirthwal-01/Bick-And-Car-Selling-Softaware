# Bick-And-Car-Selling-Softaware

This project is a **Java Swing-based desktop application** that manages a car showroom system. It provides login-based access for three roles: **Admin**, **Staff**, and **Users**, each with unique privileges and panels.

 📌 Features

- Login System
  (Admin, User, Staff)

  Admin Panel
  - Add, delete, update car details
  - Manage payment records
  - View and update sales records
  - View staff details
    Staff Panel
  - Update car prices and models
  - View own profile details
  - Interact with users
  User Panel
  - View cars
  - Book and cancel bookings
  - Search cars
  - Submit feedback
  - Interact with staff

---

## 🛠️ Technologies Used

- Java (Swing)
- MySQL (JDBC)
- JDBC Driver: `com.mysql.cj.jdbc.Driver`
- GUI Toolkit: `javax.swing`

---

## ⚙️ Requirements

- Java 8 or above
- MySQL Server
- MySQL Connector JAR added to classpath

---

## 💾 Database Configuration

Create a MySQL database named `login_db` and run the following tables:

```sql
CREATE DATABASE login_db;

USE login_db;

CREATE TABLE login (
    username VARCHAR(40),
    password VARCHAR(40)
);

CREATE TABLE car (
    carname VARCHAR(50),
    model VARCHAR(50),
    caryear INT,
    price DOUBLE,
    colour VARCHAR(30),
    bookid INT
);

CREATE TABLE Staff (
    staffid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    position VARCHAR(50),
    contact VARCHAR(15),
    address TEXT
);

CREATE TABLE payment_record (
    customer_name VARCHAR(50),
    payment_date DATE,
    amount DOUBLE
);

CREATE TABLE sales_record (
    carname VARCHAR(50),
    customer_name VARCHAR(50),
    price DOUBLE,
    sale_date DATE
);


How to Run
    Compile the program: 
            javac -cp ".;mysql-connector-java-8.0.33.jar" Mainclass.java

    Run the program:
         java -cp ".;mysql-connector-java-8.0.33.jar" Mainclass


