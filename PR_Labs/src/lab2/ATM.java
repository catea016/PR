package lab2;

import java.io.Serializable;
import java.util.ArrayList;

public class ATM implements Serializable {


    public int AccountNumber; // 6 numbers
    public int pin; // 4 numbers
    public int Request;
    public int amount;
    private int balance;


    static BankAccount bankAccount1 = new BankAccount(000001, 1111, 5000);
    static BankAccount bankAccount2 = new BankAccount(000002, 2222, 7000);
    static BankAccount bankAccount3 = new BankAccount(000003, 3333, 3000);
    static BankAccount bankAccount4 = new BankAccount(000004, 4444, 10000);
    static BankAccount bankAccount5 = new BankAccount(000005, 5555, 2000);

    static ArrayList<BankAccount> bankAccounts = new ArrayList<>();

    static {
        bankAccounts.add(bankAccount1);
        bankAccounts.add(bankAccount2);
        bankAccounts.add(bankAccount3);
        bankAccounts.add(bankAccount4);
        bankAccounts.add(bankAccount5);
    }

    public ATM() {
    }

    public ATM(int AccountNumber, int pin) {
        this(0, AccountNumber, pin, -1);
    }

    public ATM(int Request, int AccountNumber, int pin, int amount) {
        this.AccountNumber = AccountNumber;
        this.pin = pin;
        this.Request = Request;
        this.amount = amount;
    }


    public void setAccountNumber(int AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setRequest(int Request) {
        this.Request = Request;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }



    public int getAccountNumber() {
        return AccountNumber;
    }

    public int getPin() {
        return pin;
    }

    public int getRequest() {
        return Request;
    }

    public int getAmount() {
        return amount;
    }

    public int getBalance() {
        return balance;
    }

}
