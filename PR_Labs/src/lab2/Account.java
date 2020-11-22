package lab2;

public class Account {
    
    private int accountNum;
    private int pin;
    private int balance;

    public Account(int accountNum, int pin, int balance) {
        this.accountNum = accountNum;
        this.pin = pin;
        this.balance = balance;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public int getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setAccountNum(int accountNum) {
        this.accountNum = accountNum;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    
}
