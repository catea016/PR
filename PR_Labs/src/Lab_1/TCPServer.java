package Lab_1;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

class TCPServer {
    private static final String informationString = "\nEnter column_name and value to see all the results with that value(for example id 1 / first_name Stoddard)\n";

    public void start(int port) {
        try {
            ParseJson parseJson = new ParseJson();
            Main main = new Main();
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ArrayList<String> arrayList = main.getDataList();
            ArrayList<String> output;
            String inputLine;
            boolean exit = true;
            out.println(informationString);
            while ((inputLine = in.readLine()) != null && exit) {
                ArrayList<String> words = new ArrayList<>(Arrays.asList(inputLine.split(" ")));
                switch (words.size()) {
                    case 1: {
                        if ("exit".equals(inputLine)) {
                            out.println("Have a nice day :)");
                            exit = false;
                        } else {
                            output = parseJson.getValuesForGivenKey(arrayList, inputLine);
                            for (String outputElement : output) {
                                out.println(outputElement);
                            }
                            out.println(informationString);
                        }
                        break;
                    }
                    case 2: {
                        output = parseJson.getJsonForGivenValue(arrayList, words.get(0), words.get(1));
                        for (String outputElement : output) {
                            out.println(outputElement);
                        }
                        out.println(informationString);
                        break;
                    }
                    default: {
                        out.println("Unknown command!");
                        out.println(informationString);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
