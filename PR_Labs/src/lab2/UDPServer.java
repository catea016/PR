package lab2;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class UDPServer {
    // Server UDP socket runs at this port
    public final static int port = 5005;
    static public int bufSize = 512;
    static DiffieHelman dh = new DiffieHelman();

    static public void main(String args[]) {

        DatagramSocket serverSocket;

        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException se) {
            System.err.println("Cannot create socket with port " + port);
            return;
        }

        // create DatagramPacket object for receiving data:
        DatagramPacket receivedPacket = new DatagramPacket(new byte[bufSize], bufSize);

        Map<InetAddress, Integer> openSessions = new HashMap<>();
        dh.setReceiverPublicKey(dh.getPublicKey());

        while (true) {
            try {
                receivedPacket.setLength(bufSize);
                serverSocket.receive(receivedPacket);
                System.out.println("message from : " + receivedPacket.getAddress().getHostAddress());
                byte[] receivedData = receivedPacket.getData();
                byte[] decReceivedData = dh.decrypt(receivedData);
                ErrorChecking checksum = new ErrorChecking();
                checksum.getChecksumCRC32(decReceivedData);

                ByteArrayInputStream in = new ByteArrayInputStream(decReceivedData);
                ObjectInputStream is = new ObjectInputStream(in);
                AtmClient atmMessage = (AtmClient) is.readObject();
                System.out.println("Message received.");

                int requestType = atmMessage.getRequest();
                Integer value = openSessions.get(receivedPacket.getAddress());

                switch (requestType) {

                    case 0: //Login
                        if(value != null) {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            System.out.println("This address already has an account signed in!");
                            break;
                        }
                        else {
                            int count = ClientDatabase.accounts.size();
                            Boolean foundAccount = false;
                            for (int i = 0; i <= count; i++) {
                                if (ClientDatabase.accounts.get(i).getAccountNum() == atmMessage.getAccountNumber()) {
                                    if (ClientDatabase.accounts.get(i).getPin() == atmMessage.getPin()) {
                                        openSessions.put(receivedPacket.getAddress(), atmMessage.getAccountNumber());
                                        sendRequestResponse(5, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                        System.out.println( "Client has successfully log with account nr. " + atmMessage.getAccountNumber());
                                        foundAccount = true;
                                        break;
                                    } else {
                                        sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                        System.out.println("Wrong PIN for account nr. " + atmMessage.getAccountNumber());
                                        break;
                                    }
                                }
                            }
                            if (foundAccount == false) {
                                sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                System.out.println("The account number #" + atmMessage.getAccountNumber() + " does not exist!");
                                break;
                            }
                            break;
                        }

                    case 1: //Balance
                        if (value != null) {
                            for (Account account : ClientDatabase.accounts) {
                                if (account.getAccountNum() == value) {
                                    sendRequestResponse(5, account.getBalance(), receivedPacket.getAddress(), receivedPacket.getPort());
                                    System.out.println( value + "'s account balance is " + account.getBalance());
                                    break;
                                }
                            }

                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            System.out.println("Not Logged In!");

                        }
                        break;
                    case 2: //Withdraw
                        if (value != null) {
                            for (Account account : ClientDatabase.accounts) {
                                if (account.getAccountNum() == value) {
                                    if (account.getBalance() > atmMessage.getAmount()) {
                                        account.setBalance((account.getBalance() - atmMessage.getAmount()));
                                        sendRequestResponse(5, account.getBalance(), receivedPacket.getAddress(), receivedPacket.getPort());
                                        System.out.println("After the withdraw, account " + value + "'s balance is " + account.getBalance());
                                        break;
                                    }
                                    else {
                                        sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                        System.out.println("Not enough resources for this withdrawal!");
                                        break;
                                    }
                                }
                                else {
                                    System.out.println("Account not found!");
                                }
                            }
                            break;
                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            System.out.println("Not Logged In!");
                            break;
                        }
                    case 3: //Deposit
                        if (value != null) {
                            for (Account account : ClientDatabase.accounts) {
                                if (account.getAccountNum() == value) {
                                    account.setBalance((account.getBalance() + atmMessage.getAmount()));
                                    sendRequestResponse(5, account.getBalance(), receivedPacket.getAddress(), receivedPacket.getPort());
                                    System.out.println("After the deposit, account #" + value + "'s balance is " + account.getBalance());
                                    break;
                                }
                                else {
                                    sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                    System.out.println("Account not found!");
                                    break;
                                }
                            }
                            break;
                        }
                        else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            System.out.println("Not Logged In!");
                            break;
                        }

                    case 4: //Logout
                        if (value != null) {
                            openSessions.remove(receivedPacket.getAddress());
                            sendRequestResponse(5, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            System.out.println("Account nr." + value + "(" + receivedPacket.getAddress() + ") has logged out of their session!");
                            break;
                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            System.out.println("Not Logged In!");
                            break;
                        }

                    default:
                        sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                        System.out.println("Error. Not valid request :(");
                        break;
                }

                value = null;

            } catch (SocketTimeoutException ste) {    // receive() timed out
                System.err.println("Response timed out!");
            } catch (Exception ioe) {                // should never happen!
                System.err.println("Bad receive");
                ioe.printStackTrace();
            }

        }

    }

    static public void sendRequestResponse(int responseType, int responseAmount, InetAddress clientAddress, int clientPort) throws IOException {

        DatagramSocket socket;
        socket = new DatagramSocket();
        AtmClient response = new AtmClient(responseType, -1, -1, responseAmount);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(response);
        byte[] sendingData = outputStream.toByteArray();
        ErrorChecking checksum = new ErrorChecking();
        checksum.getChecksumCRC32(sendingData);
        DatagramPacket sendingPacket = new DatagramPacket(sendingData, sendingData.length, clientAddress, clientPort);
        socket.send(sendingPacket);
    }

}