package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class BankAccount {
    private Long accountId;
    private Long userId;
    private double balance;


    public BankAccount(Long accountId, Long userId, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    //default new user account set balance to $1000
    public BankAccount(Long accountId, Long userId){
        this.accountId = accountId;
        this.userId = userId;
        balance = 1000.00;
    }


    public Long getAccountId() {
        return accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

public String account() {
        return ("Account ID: " + accountId + ", User ID: " + userId + ", Available Balance: " + balance);

}

public void transfer(double amount){
        if ( amount <= 0) {
            System.err.println("Cannot transfer negative amounts. Please enter a different amount");

        } else {
            balance += amount;
            System.out.println("Amount has been transferred" + amount);
        }
}

public void send (double amount) {
        if(balance >= amount) {
            balance -= amount;
            System.out.println("Amount has been sent:" + amount);
        }else {
            System.err.println("Transaction cancelled due to insufficient funds. Check balance or deposit funds.");
}

}