package lab2;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class UDPServer {
    // Server UDP socket runs at this port
    public final static int port = 5005;
    static public int bufSize = 512;

    static public void main(String args[]) {

        DatagramSocket serverSocket;

        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Waiting for a client to connect ... ");
        } catch (SocketException se) {
            System.err.println("Cannot create socket with port " + port);
            return;
        }

        // create DatagramPacket object for receiving data:
        DatagramPacket receivedPacket = new DatagramPacket(new byte[bufSize], bufSize);

        Map<InetAddress, Integer> openSessions = new HashMap<>();
        DiffieHelman dh = new DiffieHelman();
        dh.setReceiverPublicKey(dh.getPublicKey());

        while (true) {
            try {
                receivedPacket.setLength(bufSize);
                serverSocket.receive(receivedPacket);
                byte[] receivedData = receivedPacket.getData();
                byte[] decReceivedData = dh.decrypt(receivedData);
                ErrorChecking checksum = new ErrorChecking();
                checksum.getCRC32(decReceivedData);
               // System.out.println(checksum.getChecksumCRC32(receivedData));

                ByteArrayInputStream in = new ByteArrayInputStream(decReceivedData);
                ObjectInputStream is = new ObjectInputStream(in);
                ATM atm = (ATM) is.readObject();
                int requestType = atm.getRequest();
                Integer value = openSessions.get(receivedPacket.getAddress());

                switch (requestType) {

                    case 0: //Login
                        if (value != null) {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            break;
                        } else {
                            int count = atm.bankAccounts.size();
                            Boolean foundAccount = false;
                            for (int i = 0; i <= count; i++) {
                                if (ATM.bankAccounts.get(i).getAccountNum() == atm.getAccountNumber()) {
                                    if (atm.bankAccounts.get(i).getPin() == atm.getPin()) {
                                        openSessions.put(receivedPacket.getAddress(), atm.getAccountNumber());
                                        sendRequestResponse(5, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                        foundAccount = true;
                                        break;
                                    } else {
                                        sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                        break;
                                    }
                                }
                            }
                            if (foundAccount == false) {
                                sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                break;
                            }
                            break;
                        }

                    case 1: //Balance
                        if (value != null) {
                            for (BankAccount bankAccount : atm.bankAccounts) {
                                if (bankAccount.getAccountNum() == value) {
                                    sendRequestResponse(5, bankAccount.getBalance(), receivedPacket.getAddress(), receivedPacket.getPort());
                                    break;
                                }
                            }

                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());

                        }
                        break;
                    case 2: //Withdraw
                        if (value != null) {
                            for (BankAccount bankAccount : atm.bankAccounts) {
                                if (bankAccount.getAccountNum() == value) {
                                    if (bankAccount.getBalance() > atm.getAmount()) {
                                        bankAccount.setBalance((bankAccount.getBalance() - atm.getAmount()));
                                        sendRequestResponse(5, bankAccount.getBalance(), receivedPacket.getAddress(), receivedPacket.getPort());
                                        break;
                                    } else {
                                        sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                        break;
                                    }
                                } else {
                                    //System.out.println("Account not found !");
                                }
                            }
                            break;
                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            break;
                        }
                    case 3: //Deposit
                        if (value != null) {
                            for (BankAccount bankAccount : atm.bankAccounts) {
                                if (bankAccount.getAccountNum() == value) {
                                    bankAccount.setBalance((bankAccount.getBalance() + atm.getAmount()));
                                    sendRequestResponse(5, bankAccount.getBalance(), receivedPacket.getAddress(), receivedPacket.getPort());
                                    break;
                                } else {
                                    sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                                    break;
                                }
                            }
                            break;
                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            break;
                        }

                    case 4: //Logout
                        if (value != null) {
                            openSessions.remove(receivedPacket.getAddress());
                            sendRequestResponse(5, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            break;
                        } else {
                            sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
                            break;
                        }

                    default:
                        sendRequestResponse(6, -1, receivedPacket.getAddress(), receivedPacket.getPort());
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
        ATM response = new ATM(responseType, -1, -1, responseAmount);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(response);
        byte[] sendingData = outputStream.toByteArray();
        ErrorChecking checksum = new ErrorChecking();
        checksum.getCRC32(sendingData);
        DatagramPacket sendingPacket = new DatagramPacket(sendingData, sendingData.length, clientAddress, clientPort);
        socket.send(sendingPacket);
    }

}