package lab2;

//import example.UDPSocketClient;

import java.io.IOException;
import java.io.Serializable;

public class AtmClient implements Serializable {

    public int AccountNumber; // 6 numbers
    public int pin; // 4 numbers
    public int Request;
    public int amount;
    private int balance;
    private String clientRequest;

    public AtmClient() {
    }

    public AtmClient(int AccountNumber, int pin) {
        this(0, AccountNumber, pin, -1);
    }

    public AtmClient(int Request, int AccountNumber, int pin, int amount) {
        this.AccountNumber = AccountNumber;
        this.pin = pin;
        this.Request = Request;
        this.amount = amount;
    }

    // setters
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

    public void setClientRequest(String clientRequest) {
        this.clientRequest = clientRequest;
    }

    // getters

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

    @Override
    public String toString() {
        return "ATM client: Request to Server: " + getRequest() + " \n";
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        UDPClient udp = new UDPClient();
        udp.createSocket();

    }


}
