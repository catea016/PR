package Lab_1;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MultithreadingRequest {

    private static ArrayList<String> links = new ArrayList<String>();
    private static final String initialURl = "http://localhost:5000/home";
    private static int index = 0;
    private static ArrayList<String> dataList = new ArrayList<>();
    private static volatile int nrOfThreads = 0;
    private static final int serverPort = 5005;

    public ArrayList<String> getDataList() {
        return dataList;
    }

    public static void main(String[] args) {

        AccessToken accessToken = new AccessToken();
        accessToken.requestAccessToken();
        long startTime = System.currentTimeMillis();
        new ThreadRequest(initialURl).start();
        while (true) {
            if (nrOfThreads == 0) {
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println("Execution time = " + elapsedTime + " milliseconds" + "\nEnter in telnet port through command line and type: >> telnet localhost " + serverPort);
                TCPServer tcpServer = new TCPServer();
                tcpServer.start(serverPort);
                break;
            }
        }
    }

    static class ThreadRequest extends Thread {
        private String url;
        //static int var = 0;
        ThreadRequest(String url) {
            this.url = url;
            nrOfThreads++;
            //var++;
            //System.out.println("nr of threads" + var);
        }

        @Override
        public void run() {
            RequestData requestData = new RequestData();
            AccessToken accessToken = new AccessToken();

            String result = "";
            int responseCode = 200;
            try {
                //create new http connection and put the access token in HTTP header under the key X-Access-Token
                URL siteURL = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) siteURL.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("X-Access-Token", accessToken.getAccessToken());
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    String data = requestData.readData(httpURLConnection);
                    //begin to read all routes
                    if (requestData.getRoutes(data) != null)
                        links.addAll(requestData.getRoutes(data));
                    result = " ->  Successful response";
                    System.out.println(url + result + "     type = " + requestData.getType(data));
                    if (requestData.getData(data) != null) {
                        convert(requestData.getType(data), requestData.getData(data));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //initialize new thread for each route in links list and then decrement the nrOfThreads
            if (links != null) {
                for (int i = index; i < links.size(); i++) {
                    new ThreadRequest(links.get(i)).start();
                    index++;
                }
            }
            nrOfThreads--;

        }

        void convert(String type, String data) {
            ConverterToJson converter = new ConverterToJson();
            switch (type) {
                //convert data to a unique format, json

                case "application/xml": {
                    dataList.add(converter.convertXMLtoJSON(data));
                    break;
                }

                case "text/csv": {
                    dataList.add(converter.convertCSVtoJSON(data));
                    break;
                }

                case "application/x-yaml": {
                    dataList.add(converter.convertYamlToJson(data));
                    break;
                }
                case "json": {
                    dataList.add(data);
                    break;
                }
                default:
                    System.out.println("default type");
                    break;
            }
        }
    }
}