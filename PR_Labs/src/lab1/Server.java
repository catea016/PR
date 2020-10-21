package lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {

    public void run(int port) {
        try {
            DataReader dataReader = new DataReader();
            Request request = new Request();
            //create a list for all data from files
            ArrayList<String> dataFromFiles = request.getDataFromFiles();
            ArrayList<String> response;
            String inputLine;
            boolean exitStatement = true;
            ServerSocket serverSocket = new ServerSocket(port);
            // socket object to receive incoming client requests
            Socket clientSocket = serverSocket.accept();

            System.out.println("A new client is connected : " + clientSocket);
            InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(input);
            String line = reader.readLine();    // reads a line of text


            while ((inputLine = line) != null && exitStatement == true) {
                if ("Exit".equals(inputLine)) {
                    out.println("Have a nice day :)");
                    exitStatement = false;
                } else {
                    response = getValuesForGivenColumn(dataFromFiles, inputLine);
                    for (String outputElement : response) {
                        out.println(outputElement);
                    }
                    //out.println("Introduce the next element you are interested in: \n");

                }
                /*input.close();
                clientSocket.close();
                serverSocket.close();*/

            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    ArrayList<String> getValuesForGivenColumn(ArrayList<String> dataFromFiles, String column) {
        JSONArray jsonArray = null;
        ArrayList<String> jsonValueList = new ArrayList<>();
        for (String jsonArrayStr : dataFromFiles) {
            try {
                //Convert string with index in brackets to JSON array
                jsonArrayStr = jsonArrayStr.substring(jsonArrayStr.indexOf("["), jsonArrayStr.indexOf("]") + 1);
                jsonArray = new JSONArray(jsonArrayStr);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    if (!jsonObject.optString(column).equals(""))
                        jsonValueList.add(jsonObject.optString(column));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        Collections.sort(jsonValueList);
        return jsonValueList;
    }
}
