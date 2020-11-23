package lab2;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient extends AtmClient {
    /* The server port to which
    the client socket is going to connect */
    public final static int port = 5005;
    private static final int TIMEOUT = 3000;   // Resend timeout (milliseconds)
    private static final int MAXTRIES = 5;     // Maximum number of retransmissions

    DatagramSocket clientSocket;

    public UDPClient() {}

    public void createSocket() throws IOException, ClassNotFoundException {

        try {
            Scanner input = new Scanner(System.in);
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIMEOUT);
            DiffieHelman dh = new DiffieHelman();
            dh.setReceiverPublicKey(dh.getPublicKey());

            InetAddress IPAddress = InetAddress.getByName("localhost");

            System.out.println("HELLO! Which operation would you like to perform? (Please enter only the number of operation)"
                    + "\n" + " 0: login " + "\n" +" 1: balance" + "\n"+" 2: withdraw" + "\n" + " 3: deposit" + "\n" + " 4: exit");
            while (true) {

                Request = input.nextInt();
                //0: login,  1: balance , 2: withdraw, 3: deposit , 4: exit

                AtmClient atmClient = new AtmClient(); // initializes client

                switch (Request) {
                    case 0: // login
                        atmClient.setClientRequest("Login");
                        System.out.println("Enter account number");
                        AccountNumber = input.nextInt();
                        System.out.println("Enter account pin");
                        pin = input.nextInt();
                        atmClient = new AtmClient(AccountNumber, pin);
                        System.out.println(atmClient.toString());
                        break;

                    case 1: // 1: balance
                        atmClient.setClientRequest("Balance");
                        System.out.println("Asking server for balance");
                        atmClient = new AtmClient(1, -1, -1, -1);
                        System.out.println(atmClient.toString());
                        break;

                    case 2: // 2: withdraw
                        atmClient.setClientRequest("Withdraw");
                        System.out.println("How much would you like to withdraw?");
                        amount = input.nextInt();
                        System.out.println("Withdrawing amount: " + amount);
                        atmClient = new AtmClient(2, -1, -1, amount);
                        System.out.println(atmClient.toString());
                        break;

                    case 3: // 3: deposit
                        atmClient.setClientRequest("Deposit");
                        System.out.println("How much would you like to deposit?");
                        amount = input.nextInt();
                        System.out.println("Requesting amount: " + amount + " money to be deposited into your account.");
                        atmClient = new AtmClient(3, -1, -1, amount);
                        System.out.println(atmClient.toString());
                        break;

                    case 4: // 4: exit
                        atmClient.setClientRequest("Exit");
                        atmClient = new AtmClient(4, -1, -1, -1);
                        System.out.println("Goodbye!");
                        System.out.println(atmClient.toString());
                        break;

                    default:
                        System.out.println("invalid selection. Please try again.");
                        break;
                }

                /* below is to send the client object*/
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);

                os.writeObject(atmClient);


                byte[] sendingData = outputStream.toByteArray();
                byte[] encSendingData = dh.encrypt(sendingData);
                ErrorChecking checksum = new ErrorChecking();
                checksum.getChecksumCRC32(encSendingData);
                System.out.println(encSendingData);

                DatagramPacket sendingPacket = new DatagramPacket(encSendingData, encSendingData.length, IPAddress, port);

                byte[] receivingDataBuffer = new byte[65507];
                DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

                byte[] receivedData = receivingPacket.getData();
                byte[] decReceivedData = dh.decrypt(receivedData);

                int tries = 0;
                boolean receivedResponse = false;
                do {
                    clientSocket.send(sendingPacket);          // Send the packet to server
                    try {
                        clientSocket.receive(receivingPacket);  // waiting reply from server
                        if (!receivingPacket.getAddress().equals(IPAddress)) {// Check source
                            throw new IOException("Received packet from an unknown source");
                        }else {
                            System.out.println("Message received from: " + sendingPacket.getAddress().getHostAddress());
                        }
                        receivedResponse = true;
                    } catch (InterruptedIOException e) {  // We did not get anything
                        tries += 1;
                        System.out.println("Timed out, " + (MAXTRIES - tries) + " more tries...");
                    }
                } while ((!receivedResponse) && (tries < MAXTRIES));

                if (receivedResponse) {
                    checksum.getChecksumCRC32(receivedData);
                    ByteArrayInputStream in = new ByteArrayInputStream(receivedData);
                    ObjectInputStream inputStream = new ObjectInputStream(in);
                    AtmClient atmMessage = (AtmClient) inputStream.readObject(); // reads message
                    System.out.println("Message received "); // prints message
                    switch (atmMessage.getRequest()) {
                        case 5:
                            if(Request == 0){
                                System.out.println("Successfully logged in. " + "\n" + " 0: login " + "\n" +" 1: balance" + "\n"+" 2: withdraw" + "\n" + " 3: deposit" + "\n" + " 4: exit");
                            }
                            else if (Request == 1){ // balance check
                                System.out.println("Successful: Account balance is " + atmMessage.getAmount() + ".");
                                atmMessage.setBalance(atmMessage.getAmount());
                            }else if ( Request == 2){ // withdraw
                                System.out.println("Successful: withdrawing " + amount + ". Account balance is " + atmMessage.getAmount() + ".");
                                atmMessage.setBalance(atmMessage.getAmount());
                            } else if (Request == 3) { // deposit
                                System.out.println("Successful: depositing " + amount + ". Account balance is " + atmMessage.getAmount() + ".");
                                atmMessage.setBalance(atmMessage.getAmount());
                            }else{ System.out.println("Successfully. Next client can login ");  }
                            break;

                        case 6:
                            System.out.println("There was an error with your Request. Please try again!");
                            break;

                        default:
                            System.out.println("Something get wrong!!!");
                            break;
                    }
                } else {
                    System.out.println("No response -- giving up.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientSocket.close();
    }

}