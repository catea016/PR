package lab2;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient extends ATM {

    public final static int port = 5005;
    private static final int TIMEOUT = 3000;
    private static final int MAXTRIES = 5;     // Maximum number of retransmissions

    static DatagramSocket clientSocket;


    public void createClient() {

        try {
            Scanner input = new Scanner(System.in);
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIMEOUT);
            DiffieHelman dh = new DiffieHelman();
            dh.setReceiverPublicKey(dh.getPublicKey());

            InetAddress IPAddress = InetAddress.getByName("localhost");

            System.out.println("HELLO! Which operation would you like to perform? (Please enter only the number of operation)"
                    + "\n" + " 0: login " + "\n" + " 1: balance" + "\n" + " 2: withdraw" + "\n" + " 3: deposit" + "\n" + " 4: exit");
            while (true) {

                Request = input.nextInt();
                //0: login,  1: balance , 2: withdraw, 3: deposit , 4: exit

                ATM atm = new ATM(); // initializes  atm client

                switch (Request) {
                    case 0: // login
                        System.out.println("Enter account number");
                        AccountNumber = input.nextInt();
                        System.out.println("Enter account pin");
                        pin = input.nextInt();
                        atm = new ATM(AccountNumber, pin);
                        break;

                    case 1: // 1: balance
                        System.out.println("Asking server for balance");
                        atm = new ATM(1, -1, -1, -1);
                        break;

                    case 2: // 2: withdraw
                        System.out.println("How much would you like to withdraw?");
                        amount = input.nextInt();
                        System.out.println("Withdrawing amount: " + amount);
                        atm = new ATM(2, -1, -1, amount);
                        break;

                    case 3: // 3: deposit
                        System.out.println("How much would you like to deposit?");
                        amount = input.nextInt();
                        System.out.println("Requesting amount: " + amount + " money to be deposited into your account.");
                        atm = new ATM(3, -1, -1, amount);
                        break;

                    case 4: // 4: exit
                        atm = new ATM(4, -1, -1, -1);
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("invalid selection. Please try again.");
                        break;
                }

                // send the client object
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);

                os.writeObject(atm);


                byte[] sendingData = outputStream.toByteArray();
                byte[] encSendingData = dh.encrypt(sendingData);
                ErrorChecking checksum = new ErrorChecking();
                checksum.getCRC32(encSendingData);
                //System.out.println(checksum.getChecksumCRC32(encSendingData));


                DatagramPacket sendingPacket = new DatagramPacket(encSendingData, encSendingData.length, IPAddress, port);

                byte[] receivingDataBuffer = new byte[65507];
                DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

                byte[] receivedData = receivingPacket.getData();
                // implementing retransmission
                int tries = 0;
                boolean receivedResponse = false;
                do {
                    clientSocket.send(sendingPacket);
                    try {
                        clientSocket.receive(receivingPacket);
                        if (!receivingPacket.getAddress().equals(IPAddress)) {// Check source
                            throw new IOException("Received packet from an unknown source");
                        } else {
                            System.out.println("Message successfully received from: " + sendingPacket.getAddress().getHostAddress());
                        }
                        receivedResponse = true;
                    } catch (InterruptedIOException e) {  // in case we did not get anything
                        tries += 1;
                        System.out.println("Timed out, " + (MAXTRIES - tries) + " more tries...");
                    }
                } while ((!receivedResponse) && (tries < MAXTRIES));

                if (receivedResponse) {
                    checksum.getCRC32(receivedData);
                    ByteArrayInputStream in = new ByteArrayInputStream(receivedData);
                    ObjectInputStream inputStream = new ObjectInputStream(in);
                    ATM responseMessage = (ATM) inputStream.readObject();
                    switch (responseMessage.getRequest()) {
                        case 5:
                            if (Request == 0) {
                                System.out.println("Successfully logged in. " + "\n" + " 0: login " + "\n" + " 1: balance" + "\n" + " 2: withdraw" + "\n" + " 3: deposit" + "\n" + " 4: exit");
                            } else if (Request == 1) { // balance
                                System.out.println("Account balance is " + responseMessage.getAmount() + ".");
                                responseMessage.setBalance(responseMessage.getAmount());
                            } else if (Request == 2) { // withdraw
                                System.out.println("Withdrawing " + amount + ". Account balance is " + responseMessage.getAmount() + ".");
                                responseMessage.setBalance(responseMessage.getAmount());
                            } else if (Request == 3) { // deposit
                                System.out.println("Depositing " + amount + ". Account balance is " + responseMessage.getAmount() + ".");
                                responseMessage.setBalance(responseMessage.getAmount());
                            } else if (Request == 4) {
                                System.out.println("Successfully logout. Next client can login ");
                            }
                            break;

                        case 6:
                            if (Request == 0) {
                                System.err.println("Wrong credentials (pin/account number) or your account doesn't exist. Please check the data again");
                            } else if (Request == 1) {
                                System.err.println("You need first to login if you want to check other operations");
                            } else if (Request == 2) {
                                System.err.println("Not enough resources for this withdrawal! Or some problems with your account appear");
                            } else if (Request == 3){
                                System.err.println("We didn't fond your account, you need to login first");
                            } else if (Request == 4){
                                System.err.println("You are not logged in");
                            } else {
                                System.err.println("Error. Not valid request :(");
                            }
                            break;
                    }
                } else {
                    System.err.println("No response -- giving up.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientSocket.close();
    }

    public static void main(String[] args) {
        UDPClient client = new UDPClient();
        client.createClient();
    }

}