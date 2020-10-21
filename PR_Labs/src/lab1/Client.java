package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("Trying to Connect to Server");
        // connect to server and extract input and output streams
        Socket s = new Socket("localhost", 5005);
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        pr.println("email");
        pr.flush();

        InputStreamReader input = new InputStreamReader(s.getInputStream());
        BufferedReader reader = new BufferedReader(input);

        /*String serverResponse = reader.readLine();
        System.out.println(serverResponse);*/
        char[] buf = new char[1024];
        int len = reader.read(buf);
        String response = new String(buf, 0, len);
        System.out.println(response);

    }

}
