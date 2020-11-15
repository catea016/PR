package lab2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
    // Server UDP socket runs at this port
    public final static int port = 5005;

    public static void main(String[] args) throws Exception {
        try {
            // Instantiate a new DatagramSocket to receive responses from the client
            //DiffieHelman dh = new DiffieHelman();
            DatagramSocket serverSocket = new DatagramSocket(port);
            DiffieHelman dh = new DiffieHelman();
            dh.setReceiverPublicKey(dh.getPublicKey());
            byte[] receivingDataBuffer = new byte[65507];
            byte[] sendingDataBuffer = new byte[65507];

      /* Instantiate a UDP packet to store the
      client data using the buffer for receiving data*/
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            System.out.println("Waiting for a client to connect...");

            // Receive data from the client and store in inputPacket
            serverSocket.receive(inputPacket);

            // Printing out the client sent data

            String receivedData = new String(inputPacket.getData());
            System.out.println("Encrypted data from the client: " + receivedData);
            String receivedDataDec = dh.decrypt(receivedData);
            System.out.println("Decrypted data from the client: " + receivedDataDec);

            /*
             * Convert client sent data string to upper case,
             * Convert it to bytes
             *  and store it in the corresponding buffer. */
            ErrorChecking errorChecking = new ErrorChecking();
            errorChecking.getChecksumCRC32(sendingDataBuffer);
            String sendingData = dh.encrypt("Hello from UDP server");
            sendingDataBuffer = sendingData.getBytes();

            // Obtain client's IP address and the port
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            // Create new UDP packet with data to send to the client
            DatagramPacket outputPacket = new DatagramPacket(
                    sendingDataBuffer, sendingDataBuffer.length,
                    senderAddress, senderPort
            );
            // Send the created packet to client
            serverSocket.send(outputPacket);
            serverSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}