package lab2;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient {
    /* The server port to which
    the client socket is going to connect */
    public final static int port = 5005;
    private static final int TIMEOUT = 3000;   // Resend timeout (milliseconds)
    private static final int MAXTRIES = 5;     // Maximum retransmissions


    public static void main(String[] args) throws IOException {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIMEOUT);
            // Get the IP address of the server
            InetAddress IPAddress = InetAddress.getByName("localhost");
            DiffieHelman dh = new DiffieHelman();
            dh.setReceiverPublicKey(dh.getPublicKey());

            byte[] sendingDataBuffer = new byte[65507];
            byte[] receivingDataBuffer = new byte[65507];

      /* Converting data to bytes and
      storing them in the sending buffer */
            String sendingData = dh.encrypt("Hello from UDP client");
            sendingDataBuffer = sendingData.getBytes();
            ErrorChecking checksum = new ErrorChecking();
            checksum.getChecksumCRC32(sendingDataBuffer);
            // Creating a UDP packet
            DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, port);

            // Get the server response .i.e. capitalized sentence
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            // Packets may be lost, so we have to keep trying
            int tries = 0;
            boolean receivedResponse = false;
            do {
                clientSocket.send(sendingPacket);          // Send the string to server
                try {
                    clientSocket.receive(receivingPacket);  // waiting reply from server

                    if (!receivingPacket.getAddress().equals(IPAddress)) {// Check source
                        throw new IOException("Received packet from an unknown source");
                    }
                    receivedResponse = true;
                } catch (InterruptedIOException e) {  // We did not get anything
                    tries += 1;
                    System.out.println("Timed out, " + (MAXTRIES - tries) + " more tries...");
                }
            } while ((!receivedResponse) && (tries < MAXTRIES));

            if (receivedResponse) {
                String receivedData;
                receivedData = new String(receivingDataBuffer, 0, receivingPacket.getLength());

                checksum.getChecksumCRC32(receivedData.getBytes());
                // Printing the received data
                // System.out.println("Encrypted data from the server: " + receivedData);
                String receivedDataDec = dh.decrypt(receivedData);
                System.out.println("Data from the server: " + receivedDataDec);
                //System.out.println("Received: " + new String(receivingPacket.getData()));
            } else {
                System.out.println("No response -- giving up.");
            }

            clientSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}