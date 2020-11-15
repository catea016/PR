package lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient {
    /* The server port to which
    the client socket is going to connect */
    public final static int port = 5005;

    public static void main(String[] args) throws IOException {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
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

            // Creating a UDP packet
            DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, port);

            clientSocket.send(sendingPacket);

            // Get the server response .i.e. capitalized sentence
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);

            // Printing the received data
            String receivedData = new String(receivingPacket.getData());
            System.out.println("Encrypted data from the server: " + receivedData);
            String receivedDataDec = dh.decrypt(receivedData);
            System.out.println("Decrypted data from the server: " + receivedDataDec);
            clientSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}